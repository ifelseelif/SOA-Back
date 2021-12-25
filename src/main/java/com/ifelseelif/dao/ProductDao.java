package com.ifelseelif.dao;

import com.ifelseelif.Constants;
import com.ifelseelif.dao.models.*;
import com.ifelseelif.servlets.exceptions.HttpException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDao extends Dao<Product> {

    public Product findById(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        }
    }

    public void save(Product product) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(product);
        tx1.commit();
        session.close();
    }

    public void update(Product product) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(product);
        tx1.commit();
        session.close();
    }

    public void delete(Product product) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(product);
        tx1.commit();
        session.close();
    }

    public void deleteByManufactureCost(Long manufactureCost) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        // create delete
        CriteriaDelete<Product> delete = cb.
                createCriteriaDelete(Product.class);

        // set the root class
        Root<Product> e = delete.from(Product.class);

        // set where clause
        delete.where(cb.equal(e.get("manufactureCost"), manufactureCost));

        // perform update
        session.createQuery(delete).executeUpdate();
        tx1.commit();
        session.close();
    }

    public List<Product> findAllFiltering(Filter filter) throws HttpException {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
            Root<Product> from = criteriaQuery.from(Product.class);
            Join<Product, Organization> joinCoordinates = from.join("manufacturer");
            Join<Product, Coordinates> coordinatesJoin = from.join("coordinates");
            criteriaQuery.orderBy(getListOfOrders(filter.getSortingParams(), from, coordinatesJoin, joinCoordinates, criteriaBuilder));

            Predicate predicate = getPredicate(filter.getFilters(), from, coordinatesJoin, joinCoordinates, criteriaBuilder);
            criteriaQuery.where(predicate);

            int size = filter.getPageSize();
            int index = filter.getPageIndex();

            if (size < 0) {
                throw new HttpException("Page size can not be less than 0", 400);
            }
            if (index < 0) {
                throw new HttpException("Index page can not be less than 0", 400);
            }

            TypedQuery<Product> typedQuery = session.createQuery(criteriaQuery);
            typedQuery.setFirstResult(index * size);
            typedQuery.setMaxResults(size);

            return typedQuery.getResultList();
        }
    }

    private List<Order> getListOfOrders(List<String> sortingParams, Root<Product> from, Join<Product, Coordinates> joinCoordinates, Join<Product, Organization> joinOrganization, CriteriaBuilder criteriaBuilder) {
        List<Order> orderList = new ArrayList<>();

        if (sortingParams == null) return orderList;

        for (String sortParam : sortingParams) {
            String[] args = sortParam.split(Constants.divider);

            if ((args[0].startsWith(Constants.coordinatesPrefix)) || (args[0].startsWith(Constants.manufacturerPrefix))) {
                boolean isCoordinates = args[0].startsWith(Constants.coordinatesPrefix);
                Join<?, ?> join = isCoordinates ? joinCoordinates : joinOrganization;
                args[0] = isCoordinates ? args[0].replaceAll(Constants.coordinatesPrefix, "") : args[0].replaceAll(Constants.manufacturerPrefix, "");
                addOrder(criteriaBuilder, orderList, args, join.get(args[0]));
            } else {

                addOrder(criteriaBuilder, orderList, args, from.get(args[0]));
            }
        }

        return orderList;
    }

    public Predicate getPredicate(Map<String, String[]> filters, Root<Product> from, Join<Product, Coordinates> coordinatesJoin, Join<Product, Organization> joinOrganization, CriteriaBuilder criteriaBuilder) throws HttpException {
        List<Predicate> predicateList = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : filters.entrySet()) {
            if (entry.getKey().equals("creationDate")) {
                addTimePredicate(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            if (entry.getKey().equals("name")) {
                addPredicatesForName(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            if (entry.getKey().equals("unitOfMeasure")) {
                addEnumPredicate(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder, UnitOfMeasure.class);
                continue;
            }

            if (entry.getKey().startsWith(Constants.manufacturerPrefix)) {
                addPredicates(joinOrganization, entry.getKey().split(Constants.manufacturerPrefix)[1], entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            if (entry.getKey().startsWith(Constants.coordinatesPrefix)) {
                addPredicates(coordinatesJoin, entry.getKey().split(Constants.coordinatesPrefix)[1], entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            addPredicates(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder);
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    private void addPredicatesForName(Root<Product> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) throws HttpException {
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                if (splitCond[0].equals("like")) {
                    String pattern = "%" + splitCond[1] + "%";
                    predicateList.add(criteriaBuilder.like(from.get(propertyName), pattern));
                } else {
                    addPredicate(from, splitCond[0], propertyName, splitCond[1], predicateList, criteriaBuilder);
                }
            } catch (Exception ignored) {
                throw new HttpException("Invalid filter" + condition, 400);
            }
        }
    }
}