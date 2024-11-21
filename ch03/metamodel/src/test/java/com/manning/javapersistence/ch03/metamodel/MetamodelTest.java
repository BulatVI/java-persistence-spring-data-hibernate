package com.manning.javapersistence.ch03.metamodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MetamodelTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("ch03.metamodel");
    }

    @Test
    void accessDynamicMetamodel() {

        Metamodel metamodel = emf.getMetamodel();
        Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
        ManagedType<?> itemType = managedTypes.iterator().next();

        assertAll(() -> assertEquals(1, managedTypes.size()),
                () -> assertEquals(
                        Type.PersistenceType.ENTITY,
                        itemType.getPersistenceType()));

        SingularAttribute<?, ?> idAttribute = itemType.getSingularAttribute("id");
        assertFalse(
                idAttribute.isOptional());

        SingularAttribute<?, ?> nameAttribute = itemType.getSingularAttribute("name");

        assertAll(() -> assertEquals(String.class, nameAttribute.getJavaType()),
                () -> assertEquals(
                        Attribute.PersistentAttributeType.BASIC,
                        nameAttribute.getPersistentAttributeType()));

        SingularAttribute<?, ?> auctionEndAttribute = itemType.getSingularAttribute("auctionEnd");
        assertAll(() -> assertEquals(Date.class, auctionEndAttribute.getJavaType()),
                () -> assertFalse(auctionEndAttribute.isCollection()),
                () -> assertFalse(auctionEndAttribute.isAssociation()));

    }

    @Test
    void accessStaticMetamodel() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        query.select(fromItem);
        List<Item> items = em.createQuery(query).getResultList();

        assertEquals(2, items.size());
    }

    @Test
    void testItemsPattern() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        Path<String> namePath = fromItem.get("name");
        query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));
        List<Item> items = em.createQuery(query).setParameter("pattern", "%Item 1%").getResultList();
        assertAll(() -> assertEquals(1, items.size()),
                () -> assertEquals("Item 1", items.iterator().next().getName()));
    }

    @Test
    void testItemsPatternWithGeneratedClass() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        Path<String> namePath = fromItem.get(Item_.name);
        query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));
        List<Item> items = em.createQuery(query).setParameter("pattern", "%Item 1%").getResultList();
        assertAll(() -> assertEquals(1, items.size()),
                () -> assertEquals("Item 1", items.iterator().next().getName()));
    }

    private void deleteItems(EntityManager em) {
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Item> query = cb.createCriteriaDelete(Item.class);
        query.from(Item.class);
        em.createQuery(query).executeUpdate();
        em.getTransaction().commit();
    }

    private void persistItems(EntityManager em) {
        em.getTransaction().begin();
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setAuctionEnd(tomorrow());

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setAuctionEnd(tomorrow());

        em.persist(item1);
        em.persist(item2);
        em.getTransaction().commit();
    }

    private Date tomorrow() {
        return new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
    }

    // @AfterAll
    static void afterAll() {
        emf.close();
    }
}
