package com.ifelseelif.database.repositories.interfaces;

import com.ifelseelif.Constants;
import com.ifelseelif.database.models.Filter;
import com.ifelseelif.servlets.exceptions.BadRequestException;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public abstract class Repository<T> {
    private final Class<T> typeParameterClass;
    protected final EntityManager entityManager;

    public Repository(Class<T> type) {
        typeParameterClass = type;
        entityManager = Persistence.createEntityManagerFactory("soa").createEntityManager();
    }

    public Optional<T> save(T t) {
        entityManager.getTransaction().begin();
        entityManager.persist(t);
        entityManager.getTransaction().commit();
        return Optional.of(t);
    }

    public Optional<T> getById(Object id) {
        if (id == null) return Optional.empty();

        T t = entityManager.find(typeParameterClass, id);
        return t != null ? Optional.of(t) : Optional.empty();
    }

    public boolean deleteById(Object id) throws BadRequestException {
        T t = entityManager.find(typeParameterClass, id);
        if (t != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(t);
                entityManager.getTransaction().commit();
            } catch (RollbackException e) {
                throw new BadRequestException("You must delete before products which contains this organization");
            }
            return true;
        }
        return false;
    }

    public void update(T t) {
        entityManager.getTransaction().begin();
        entityManager.merge(t);
        entityManager.getTransaction().commit();
    }

    protected <Y extends Enum<Y>> void addEnumPredicate(Root<T> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder, Class<Y> enumClass) throws BadRequestException {
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                Y value = Enum.valueOf(enumClass, splitCond[1]);
                addPredicate(from, splitCond[0], propertyName, value, predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new BadRequestException("Invalid filter " + condition);
            }
        }
    }

    protected void addTimePredicate(Root<T> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) throws BadRequestException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                java.util.Date date = formatter.parse(splitCond[1]);
                System.out.println(date);
                addPredicate(from, splitCond[0], propertyName, date, predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new BadRequestException("Invalid filter " + condition);
            }
        }
    }

    protected <Z, X> void addPredicates(From<Z, X> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) throws BadRequestException {
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                addPredicate(from, splitCond[0], propertyName, splitCond[1], predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new BadRequestException("Invalid filter " + condition);
            }
        }
    }

    protected <Y extends Comparable<? super Y>, Z, X> void addPredicate(From<Z, X> from, String condition, String propertyName, Y value, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) {
        switch (condition) {
            case ">":
                predicateList.add(criteriaBuilder.greaterThan(from.get(propertyName), value));
                break;
            case "<":
                predicateList.add(criteriaBuilder.lessThan(from.get(propertyName), value));
                break;
            case ">=":
                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(from.get(propertyName), value));
                break;
            case "<=":
                predicateList.add(criteriaBuilder.lessThanOrEqualTo(from.get(propertyName), value));
                break;
            case "=":
                predicateList.add(criteriaBuilder.equal(from.get(propertyName), value));
                break;
        }
    }

    protected <Field> void addOrder(CriteriaBuilder criteriaBuilder, List<Order> orderList, String[] args, Path<Field> objectPath) {
        if ((args.length == 1) || ((args.length == 2) && (args[1].equals("asc")))) {
            orderList.add(criteriaBuilder.asc(objectPath));
        } else if (args.length == 2) {
            orderList.add(criteriaBuilder.desc(objectPath));
        }
    }

    public abstract List<T> getAll(Filter filter) throws BadRequestException;
}
