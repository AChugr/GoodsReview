package ru.goodsreview.core.db.controller;

/*
 *  Date: 13.11.11
 *  Time: 11:00
 *  Author:
 *     Vanslov Evgeny
 *     vans239@gmail.com
 */

import ru.goodsreview.core.db.exception.StorageException;
import ru.goodsreview.core.model.ThesisUnique;

import java.util.List;

public interface ThesisUniqueController {
    public long addThesisUnique(ThesisUnique thesisUnique) throws StorageException;

    public List<Long> addThesisUniqueList(List<ThesisUnique> thesisUniqueList) throws StorageException;

    public ThesisUnique getThesisUniqueByContent(String content);

    public List<ThesisUnique> getAllThesesUnique();
}
