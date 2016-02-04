//********************************************************************/
//* Copyright (C) 2008  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MergeSortedArrayLists.java  0.01 08/01/2008
 */

package mmj.util;

import java.util.*;

import mmj.pa.MMJException;

/**
 * Class {@code MergeSortedArrayLists} merges elements of a sorted source into a
 * sorted destination ArrayList.
 * <p>
 * Does in-place merge of source List into the destination ArrayList using the
 * input comparator to maintain the sort sequence of the destination ArrayList.
 * <p>
 * A buffer of destination ArrayList elements is used as the "in place" merge
 * proceeds. The idea is to reduce the number of destination array element
 * shifts by making a single pass through the the destination array.
 * <p>
 * Assumes that input lists are correctly sorted. Makes no assumptions about
 * uniqueness of input keys.
 * <p>
 * If the source and destination lists contain the same key, the source element
 * replaces the destination element.
 * <p>
 *
 * @param <T> the actual type of the destination array
 */
public class MergeSortedArrayLists<T> {

    private int destGetIndex = 0;
    private int destPutIndex = 0;

    private T nextDest;
    private T nextSrc;

    private ArrayList<T> dest;

    private List<? extends T> src;
    private Iterator<? extends T> srcIterator;

    private LinkedList<T> buf;
    private int maxDestBuf;

    private int compareResult;

    /**
     * Does in-place merge of source List into the destination ArrayList using
     * the input comparator to maintain the sort sequence of the destination
     * ArrayList.
     * <p>
     * A buffer of destination ArrayList elements is used as the "in place"
     * merge proceeds. The idea is to reduce the number of destination array
     * element shifts by making a single pass through the the destination array.
     * <p>
     * Assumes that input lists are correctly sorted. Makes no assumptions about
     * uniqueness of input keys.
     * <p>
     * If the source and destination lists contain the same key, the source
     * element replaces the destination element.
     *
     * @param destList ArrayList sorted in comparator order.
     * @param srcList List sorted in comparator order.
     * @param comparator Comparator for comparing list object.
     * @param abortIfDupsFound triggers IllegalObjectException if srcList object
     *            equals a destList object
     * @throws IllegalArgumentException if a srcList object equals a destList
     *             object and abortIfDupsFound is true (the normal situation for
     *             Theorem Loader.)
     */
    public MergeSortedArrayLists(final ArrayList<T> destList,
        final List<? extends T> srcList, final Comparator<T> comparator,
        final boolean abortIfDupsFound) throws IllegalArgumentException
    {

//doh
//      Iterator iterator = destList.iterator();
//      System.out.println("destList follows");
//      while (iterator.hasNext()) {
//          Assrt assrt = (Assrt)iterator.next();
//          System.out.println(assrt.getLabel()
//                             + " "
//                             + assrt.getSeq());
//      }
//      iterator = srcList.iterator();
//      System.out.println("srcList follows");
//      while (iterator.hasNext()) {
//          Assrt assrt = (Assrt)iterator.next();
//          System.out.println(assrt.getLabel()
//                             + " "
//                             + assrt.getSeq());
//      }
//

        src = srcList;
        srcIterator = src.iterator();
        if ((nextSrc = getNextSrc()) == null)
            return;

        buf = new LinkedList<>();

        dest = destList;
        maxDestBuf = dest.size();

        dest.ensureCapacity(dest.size() + src.size());

        if ((nextDest = getNextDest()) == null) {
            finishUsingSrc();
            return;
        }

        while (true) {
            compareResult = comparator.compare(nextSrc, nextDest);
            if (compareResult > 0) { // nextDest < nextSrc
                put(nextDest);
                if ((nextDest = getNextDest()) == null) {
                    finishUsingSrc();
                    break;
                }
            }
            else { // nextSrc <= nextDest
                if (compareResult == 0) {
                    if (abortIfDupsFound)
                        throw new IllegalArgumentException(new MMJException(
                            UtilConstants.ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR,
                            nextSrc));
                    if ((nextDest = getNextDest()) == null) {
                        finishUsingSrc();
                        break;
                    }
                }
                // nextSrc < nextDest
                put(nextSrc);
                if ((nextSrc = getNextSrc()) == null) {
                    finishUsingDest();
                    break;
                }
            }
        }
    }

    private void finishUsingSrc() {
        while (nextSrc != null) {
            put(nextSrc);
            nextSrc = getNextSrc();
        }
    }

    private void finishUsingDest() {
        while (nextDest != null) {
            put(nextDest);
            nextDest = getNextDest();
        }
    }

    private void put(final T object) {
        /*
           !!! before outputting to dest, make sure that the dest
           list element is in the buffer if it is one of the
           original dest elements.
         */
        if (destPutIndex < dest.size()) {
            if (destGetIndex <= destPutIndex)
                // inline: loadNextBufElement();
                if (destGetIndex < maxDestBuf)
                    buf.addLast(dest.get(destGetIndex++));
            dest.set(destPutIndex, object);
        }
        else
            dest.add(object);
        destPutIndex++;
    }

    private T getNextSrc() {
        if (srcIterator.hasNext())
            return srcIterator.next();
        else
            return null;
    }

    private T getNextDest() {
        if (buf.isEmpty()) {
//          inline: return loadNextBufElementVirtual();
            if (destGetIndex < maxDestBuf)
                return dest.get(destGetIndex++);
            return null;
        }
        return buf.removeFirst();
    }

//  private void loadNextBufElement() {
//      if (destGetIndex < maxDestBuf) {
//          buf.addLast(dest.get(destGetIndex++));
//      }
//  }
//
//  // used when buf empty and need next element,
//  // so buffer is bypassed.
//  private Object loadNextBufElementVirtual() {
//      if (destGetIndex < maxDestBuf) {
//          return dest.get(destGetIndex++);
//      }
//      return null;
//  }

}
