package clojure.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

public class PersistentOrderedSet extends AFn
  implements IObj, IEditableCollection, IPersistentSet, Counted, IFn, IMeta, IPersistentCollection,
             Seqable, Serializable, Iterable, Runnable, Collection, Callable, Set {

static public final PersistentOrderedSet EMPTY =
  new PersistentOrderedSet(null, PersistentHashSet.EMPTY, PersistentVector.EMPTY);

final IPersistentMap   _meta;
final IPersistentSet   items;
final IPersistentVector order;

protected PersistentOrderedSet(IPersistentMap meta, IPersistentSet items, IPersistentVector order) {
  this._meta = meta;
  this.items = items;
  this.order = order;
}

static public PersistentOrderedSet create(ISeq items){
  PersistentOrderedSet set = EMPTY;
  for(; items != null; items = items.next()) {
    set = (PersistentOrderedSet) set.cons(items.first());
  }
  return set;
}

public IPersistentSet disjoin(Object item) throws Exception{
  if (!contains(item)) return this;

  PersistentVector.TransientVector new_order = PersistentVector.EMPTY.asTransient();
  ISeq s = seq();
  for (; s != null; s = s.next()) {
    if (s.first() != item) new_order = new_order.conj(s.first());
  }
  return new PersistentOrderedSet(_meta, items.disjoin(item), new_order.persistent());
}

public IPersistentSet cons(Object item){
  if (contains(item)) return this;
  return new PersistentOrderedSet(_meta, (IPersistentSet) items.cons(item), order.cons(item));
}

public IPersistentCollection empty(){
  return EMPTY.withMeta(meta());
}

public PersistentOrderedSet withMeta(IPersistentMap meta){
  return new PersistentOrderedSet(meta, items, order);
}

public IPersistentMap meta(){
  return _meta;
}

public String toString(){
  return RT.printString(this);
}

public Object get(Object key){
  return items.get(key);
}

public boolean contains(Object key){
  return items.contains(key);
}

public boolean containsAll(Collection c){
  for (Object item : c) {
    if (!contains(item)) return false;
  }
  return true;
}

public int count(){
  return order.count();
}

public int size(){
  return count();
}

public boolean isEmpty(){
  return count() == 0;
}

public ISeq seq(){
  return RT.seq(order);
}

public Iterator iterator(){
  return new SeqIterator(seq());
}

public Object invoke(Object arg1) throws Exception{
  return get(arg1);
}

public boolean equals(Object obj){
  if (this == obj) return true;
  if (!(obj instanceof Set)) return false;
  Set s = (Set) obj;

  if (s.size() != count()) return false;
  return containsAll(s);
}

public boolean equiv(Object obj){
  return equals(obj);
}

public Object[] toArray(){
  return RT.seqToArray(seq());
}

public Object[] toArray(Object[] a){
  if (count() > a.length)  return toArray();

  ISeq s = seq();
  for (int i = 0; s != null; ++i, s = s.next()) {
    a[i] = s.first();
  }
  if (a.length > count()) a[count()] = null;
  return a;
}

public boolean add(Object o){
  throw new UnsupportedOperationException();
}

public boolean remove(Object o){
  throw new UnsupportedOperationException();
}

public boolean addAll(Collection c){
  throw new UnsupportedOperationException();
}

public void clear(){
  throw new UnsupportedOperationException();
}

public boolean retainAll(Collection c){
  throw new UnsupportedOperationException();
}

public boolean removeAll(Collection c){
  throw new UnsupportedOperationException();
}

public ITransientCollection asTransient() {
  return new TransientOrderedSet((ITransientSet) ((PersistentHashSet) items).asTransient(), ((PersistentVector) order).asTransient());
}

static final class TransientOrderedSet extends AFn implements ITransientSet {
  ITransientSet items;
  ITransientVector order;

  TransientOrderedSet(ITransientSet items, ITransientVector order) {
    this.items = items;
    this.order = order;
  }

  public IPersistentSet persistent() {
    return new PersistentOrderedSet(null, (IPersistentSet) items.persistent(), (IPersistentVector) order.persistent());
  }

  public ITransientSet conj(Object obj) {
    if (contains(obj))  return this;

    ITransientSet s = (ITransientSet) items.conj(obj);
    if (s != items) items = s;

    ITransientVector v = (ITransientVector) order.conj(obj);
    if (v != order) order = v;

    return this;
  }

  public int count() {
    return items.count();
  }

  public boolean contains(Object obj) {
    // This is a workaround for the puzzling fact that
    // PersistentHashSet.EMPTY.asTransient().contains(o) for any Object o.
    return (items.count() != 0) && items.contains(obj);
  }

  public ITransientSet disjoin(Object obj) throws Exception {
    if (!contains(obj)) return this;

    ITransientSet set = items.disjoin(obj);
    if (set != items) items = set;

    PersistentVector.TransientVector new_order = PersistentVector.EMPTY
      .asTransient();
    ISeq s = order.persistent().seq();
    for (; s != null; s = s.next()) {
      if (s.first() != obj) {
        new_order = new_order.conj(s.first());
      }
    }
    if (order != new_order) order = new_order;

    return this;
  }

  public Object get(Object key) {
    return items.get(key);
  }
}
}
