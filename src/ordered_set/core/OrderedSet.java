package ordered_set.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

import clojure.lang.AFn;
import clojure.lang.Counted;
import clojure.lang.IEditableCollection;
import clojure.lang.IFn;
import clojure.lang.IMeta;
import clojure.lang.IObj;
import clojure.lang.IPersistentCollection;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.ITransientCollection;
import clojure.lang.ITransientSet;
import clojure.lang.ITransientVector;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.SeqIterator;
import clojure.lang.Seqable;
import clojure.lang.Util;


public class OrderedSet extends AFn implements IObj, IEditableCollection, IPersistentSet, Counted,
        IFn, IMeta, IPersistentCollection, Seqable, Serializable, Iterable, Runnable, Collection,
        Callable, Set {

  static public final OrderedSet EMPTY = new OrderedSet(null, PersistentHashSet.EMPTY, PersistentVector.EMPTY);

  int _hash = -1;
  final IPersistentMap _meta;
  final IPersistentSet items;
  final IPersistentCollection order;

  protected OrderedSet(IPersistentMap meta, IPersistentSet items, IPersistentCollection order) {
    this._meta = meta;
    this.items = items;
    this.order = order;
  }

  static public OrderedSet create(ISeq items) {
    OrderedSet set = EMPTY;
    for (; items != null; items = items.next()) {
      set = (OrderedSet)set.cons(items.first());
    }
    return set;
  }

  @Override
  public IPersistentSet disjoin(Object item) throws Exception {
    if (!contains(item))
      return this;

    ITransientVector new_order = PersistentVector.EMPTY.asTransient();
    for (ISeq s = seq(); s != null; s = s.next()) {
      if (!Util.equiv(item, s.first()))
        new_order = (ITransientVector)new_order.conj(s.first());
    }
    return new OrderedSet(_meta, items.disjoin(item), new_order.persistent());
  }

  @Override
  public IPersistentSet cons(Object item) {
    if (contains(item))
      return this;
    return new OrderedSet(_meta, (IPersistentSet)items.cons(item), order.cons(item));
  }

  @Override
  public IPersistentCollection empty() {
    return EMPTY.withMeta(meta());
  }

  @Override
  public OrderedSet withMeta(IPersistentMap meta) {
    return new OrderedSet(meta, items, order);
  }

  @Override
  public IPersistentMap meta() {
    return _meta;
  }

  @Override
  public String toString() {
    return RT.printString(this);
  }

  @Override
  public Object get(Object key) {
    return items.get(key);
  }

  @Override
  public boolean contains(Object key) {
    return items.contains(key);
  }

  @Override
  public boolean containsAll(Collection c) {
    for (Object item : c) {
      if (!contains(item))
        return false;
    }
    return true;
  }

  @Override
  public int count() {
    return order.count();
  }

  @Override
  public int size() {
    return count();
  }

  @Override
  public boolean isEmpty() {
    return count() == 0;
  }

  @Override
  public ISeq seq() {
    return RT.seq(order);
  }

  @Override
  public Iterator iterator() {
    return new SeqIterator(seq());
  }

  @Override
  public Object invoke(Object arg1) throws Exception {
    return get(arg1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Set))
      return false;
    Set s = (Set)obj;

    if (s.size() != count() || s.hashCode() != hashCode())
      return false;
    return containsAll(s);
  }

  @Override
  public boolean equiv(Object obj) {
    return equals(obj);
  }

  @Override
  public int hashCode() {
    if (_hash == -1) {
      int hash = 0;
      for (ISeq s = seq(); s != null; s = s.next()) {
        Object e = s.first();
        hash += Util.hash(e);
      }
      this._hash = hash;
    }
    return _hash;
  }

  @Override
  public Object[] toArray() {
    return RT.seqToArray(seq());
  }

  @Override
  public Object[] toArray(Object[] a) {
    if (count() > a.length)
      return toArray();

    ISeq s = seq();
    for (int i = 0; s != null; ++i, s = s.next()) {
      a[i] = s.first();
    }
    if (a.length > count())
      a[count()] = null;
    return a;
  }

  @Override
  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ITransientCollection asTransient() {
    return new TransientOrderedSet((ITransientSet)((PersistentHashSet)items).asTransient(), ((PersistentVector)order).asTransient());
  }

  static final class TransientOrderedSet extends AFn implements ITransientSet {
    ITransientSet items;
    ITransientVector order;

    TransientOrderedSet(ITransientSet items, ITransientVector order) {
      this.items = items;
      this.order = order;
    }

    @Override
    public IPersistentSet persistent() {
      return new OrderedSet(null, (IPersistentSet)items.persistent(), (IPersistentVector)order.persistent());
    }

    @Override
    public ITransientSet conj(Object obj) {
      if (contains(obj))
        return this;

      ITransientSet s = (ITransientSet)items.conj(obj);
      if (s != items)
        items = s;

      ITransientVector v = (ITransientVector)order.conj(obj);
      if (v != order)
        order = v;

      return this;
    }

    @Override
    public int count() {
      return items.count();
    }

    @Override
    public boolean contains(Object obj) {
      // This is a workaround for the puzzling fact that
      // PersistentHashSet.EMPTY.asTransient().contains(o) for any Object o.
      return (items.count() != 0) && items.contains(obj);
    }

    @Override
    public ITransientSet disjoin(Object obj) throws Exception {
      if (!contains(obj))
        return this;

      ITransientSet set = items.disjoin(obj);
      if (set != items)
        items = set;

      ITransientVector new_order = PersistentVector.EMPTY.asTransient();
      int max = order.count();
      for (int i = 0; i < max; i++) {
        Object item = order.valAt(i);
        if (!Util.equiv(item, obj))
          new_order = (ITransientVector)new_order.conj(item);
      }
      order = new_order;

      return this;
    }

    @Override
    public Object get(Object key) {
      return items.get(key);
    }
  }
}
