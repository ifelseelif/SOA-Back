package com.ifelseelif.dao;

import com.ifelseelif.Constants;
import com.ifelseelif.servlets.exceptions.HttpException;

import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class Dao<T> {
    protected <Y extends Enum<Y>> void addEnumPredicate(Root<T> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder, Class<Y> enumClass) throws HttpException {
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                Y value = Enum.valueOf(enumClass, splitCond[1]);
                addPredicate(from, splitCond[0], propertyName, value, predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new HttpException("Invalid filter " + condition, 400);
            }
        }
    }

    protected void addTimePredicate(Root<T> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) throws HttpException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                java.util.Date date = formatter.parse(splitCond[1]);
                System.out.println(date);
                addPredicate(from, splitCond[0], propertyName, date, predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new HttpException("Invalid filter " + condition, 400);
            }
        }
    }

    protected <Z, X> void addPredicates(From<Z, X> from, String propertyName, String[] conditions, List<Predicate> predicateList, CriteriaBuilder criteriaBuilder) throws HttpException {
        for (String condition : conditions) {
            String[] splitCond = condition.split(Constants.divider);
            if (splitCond.length != 2) return;
            try {
                addPredicate(from, splitCond[0], propertyName, splitCond[1], predicateList, criteriaBuilder);
            } catch (Exception ignored) {
                throw new HttpException("Invalid filter " + condition, 400);
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
}
