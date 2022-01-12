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

public class OrganizationDao extends Dao<Organization> {

    public Organization findById(int id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(Organization.class, id);
        }
    }

    public void save(Organization organization) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(organization);
        tx1.commit();
        session.close();
    }

    public void update(Organization organization) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(organization);
        tx1.commit();
        session.close();
    }

    public void delete(Organization organization) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(organization);
        tx1.commit();
        session.close();
    }

    public List<Organization> findAllFiltering(Filter filter) throws HttpException {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
            Root<Organization> from = criteriaQuery.from(Organization.class);
            Join<Organization, Address> joinPostalAddress = from.join("postalAddress");
            Join<Organization, Location> joinTown = joinPostalAddress.join("town");

            criteriaQuery.orderBy(getListOfOrders(filter.getSortingParams(), from, joinTown, joinPostalAddress, criteriaBuilder));

            Predicate predicate = getPredicate(filter.getFilters(), from, joinTown, joinPostalAddress, criteriaBuilder);
            criteriaQuery.where(predicate);

            int size = filter.getPageSize();
            int index = filter.getPageIndex();
            if (size < 0) {
                throw new HttpException("Page size can not be less than 0", 400);
            }
            if (index < 0) {
                throw new HttpException("Index page can not be less than 0", 400);
            }

            TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
            typedQuery.setFirstResult(index * size);
            typedQuery.setMaxResults(size);

            return typedQuery.getResultList();
        }
    }

    private Predicate getPredicate(Map<String, String[]> filters, Root<Organization> from, Join<Organization, Location> locationJoin, Join<Organization, Address> joinPostalAddress, CriteriaBuilder criteriaBuilder) throws HttpException {
        List<Predicate> predicateList = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : filters.entrySet()) {
            if (entry.getKey().equals("type")) {
                addEnumPredicate(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder, OrganizationType.class);
                continue;
            }

            if (entry.getKey().startsWith(Constants.addressPrefix)) {
                addPredicates(joinPostalAddress, entry.getKey().split(Constants.addressPrefix)[1], entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            if (entry.getKey().startsWith(Constants.townPrefix)) {
                addPredicates(locationJoin, entry.getKey().split(Constants.townPrefix)[1], entry.getValue(), predicateList, criteriaBuilder);
                continue;
            }

            addPredicates(from, entry.getKey(), entry.getValue(), predicateList, criteriaBuilder);
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    private List<Order> getListOfOrders(List<String> sortingParams, Root<Organization> from, Join<Organization, Location> locationJoin, Join<Organization, Address> joinPostalAddress, CriteriaBuilder criteriaBuilder) throws HttpException {
        List<Order> orderList = new ArrayList<>();

        if (sortingParams == null) return orderList;

        for (String sortParam : sortingParams) {
            String[] args = sortParam.split(Constants.divider);

            if ((args[0].startsWith(Constants.addressPrefix)) || (args[0].startsWith(Constants.townPrefix))) {
                boolean isAddress = args[0].startsWith(Constants.addressPrefix);
                Join<?, ?> join = args[0].startsWith(Constants.addressPrefix) ? joinPostalAddress : locationJoin;
                args[0] = isAddress ? args[0].replaceAll(Constants.addressPrefix, "") : args[0].replaceAll(Constants.townPrefix, "");
                addOrder(criteriaBuilder, orderList, args, join.get(args[0]));
            } else {
                addOrder(criteriaBuilder, orderList, args, from.get(args[0]));
            }
        }

        return orderList;
    }

}
