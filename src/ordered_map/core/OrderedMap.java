package ordered_map.core;

import java.util.Iterator;
import java.util.Map;

import clojure.lang.APersistentMap;
import clojure.lang.ASeq;
import clojure.lang.IEditableCollection;
import clojure.lang.IMapEntry;
import clojure.lang.IObj;
import clojure.lang.IPersistentCollection;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.ITransientCollection;
import clojure.lang.ITransientMap;
import clojure.lang.ITransientVector;
import clojure.lang.Obj;
import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentVector;
import clojure.lang.SeqIterator;
import clojure.lang.Util;


public class OrderedMap extends APersistentMap implements IEditableCollection, IObj {
  public final IPersistentMap mappings;
  public final IPersistentVector order;
  public final IPersistentMap meta;

  public static final OrderedMap EMPTY = new OrderedMap();

  @Override
  public IMapEntry entryAt(Object key) {
    return mappings.entryAt(key);
  }

  private OrderedMap() {
    order = PersistentVector.EMPTY;
    mappings = PersistentHashMap.EMPTY;
    meta = null;
  }

  private OrderedMap(IPersistentMap mappings, IPersistentVector order, IPersistentMap meta) {
    super();
    this.mappings = mappings;
    this.order = order;
    this.meta = meta;
  }

  @Override
  public IPersistentMap assoc(Object key, Object val) {
    IPersistentMap newMappings = mappings.assoc(key, val);
    if (newMappings == mappings) {
      return this;
    }

    return new OrderedMap(newMappings, order.cons(key), meta);
  }

  @Override
  public IPersistentMap assocEx(Object key, Object val) throws Exception {
    if (this.containsKey(key)) {
      throw new RuntimeException("Key " + key + " already present");
    }
    return assoc(key, val);
  }

  @Override
  public IPersistentMap without(Object key) throws Exception {
    IPersistentMap newMappings = mappings.without(key);
    if (newMappings == mappings) {
      return this;
    }

    ITransientVector v = PersistentVector.EMPTY.asTransient();
    for (ISeq seq = order.seq(); seq != null; seq = seq.next()) {
      Object o = seq.first();
      if (!Util.equiv(key, o)) {
        v = (ITransientVector)v.conj(o);
      }
    }

    return new OrderedMap(newMappings, (IPersistentVector)v.persistent(), meta);
  }

  @Override
  public Iterator iterator() {
    return new SeqIterator(seq());
  }

  @Override
  public boolean containsKey(Object key) {
    return mappings.containsKey(key);
  }

  @Override
  public IPersistentCollection empty() {
    if (meta != null) {
      return (IPersistentCollection)EMPTY.withMeta(meta);
    }
    return EMPTY;
  }

  @Override
  public ISeq seq() {
    class Seq extends ASeq {
      private ISeq keys;

      public Seq(ISeq keys, IPersistentMap meta) {
        super(meta);
        this.keys = keys;
      }

      @Override
      public Object first() {
        return mappings.entryAt(keys.first());
      }

      @Override
      public ISeq next() {
        ISeq more = keys.next();
        if (more == null) {
          return null;
        }
        return new Seq(more, meta);
      }

      @Override
      public Obj withMeta(IPersistentMap meta) {
        return new Seq(keys, meta);
      }
    }

    ISeq keys = order.seq();
    if (keys == null) {
      return null;
    }
    return new Seq(keys, null);
  }

  public static void main(String[] args) {
    OrderedMap m = OrderedMap.EMPTY;
    ((ITransientMap)m.asTransient()).assoc(1, 2);
  }

  private static class Transient implements ITransientMap {
    public ITransientMap mappings;
    public ITransientVector order;

    public Transient(ITransientMap mappings, ITransientVector order) {
      this.mappings = mappings;
      this.order = order;
    }

    @Override
    public Object valAt(Object key) {
      return mappings.valAt(key);
    }

    @Override
    public Object valAt(Object key, Object notFound) {
      return mappings.valAt(key, notFound);
    }

    @Override
    public int count() {
      return mappings.count();
    }

    @Override
    public IPersistentMap persistent() {
      return new OrderedMap(mappings.persistent(), (IPersistentVector)order.persistent(), null);
    }

    @Override
    public ITransientCollection conj(Object val) {
      if (val == null) {
        return this;
      }
      Map.Entry<?, ?> e = (Entry<?, ?>)val;
      return assoc(e.getKey(), e.getValue());
    }

    @Override
    public ITransientMap assoc(Object key, Object val) {
      // on 1.2 the notFound argument to transient maps is broken, so we check count() before and after to see if
      // anything has changed.
      int oldCount = count();
      mappings = mappings.assoc(key, val);
      if (count() != oldCount) {
        order = (ITransientVector)order.conj(key);
      }
      return this;
    }

    @Override
    public ITransientMap without(Object key) {
      // on 1.2 the notFound argument to transient maps is broken, so we check count() before and after to see if
      // anything has changed.
      int oldCount = count();
      mappings = mappings.without(key);
      if (count() != oldCount) {
        ITransientVector newOrder = PersistentVector.EMPTY.asTransient();
        int max = order.count();
        for (int i = 0; i < max; i++) {
          Object item = order.valAt(i);
          if (!Util.equiv(item, key)) {
            newOrder = (ITransientVector)newOrder.conj(item);
          }
        }
        this.order = newOrder;
      }
      return this;
    }
  }

  @Override
  public ITransientCollection asTransient() {
    return new Transient((ITransientMap)((IEditableCollection)mappings).asTransient(), (ITransientVector)((IEditableCollection)order).asTransient());
  }

  @Override
  public Object valAt(Object key) {
    return mappings.valAt(key);
  }

  @Override
  public int count() {
    return mappings.count();
  }

  @Override
  public Object valAt(Object key, Object notFound) {
    return mappings.valAt(key, notFound);
  }

  @Override
  public IPersistentMap meta() {
    return meta;
  }

  @Override
  public IObj withMeta(IPersistentMap meta) {
    return new OrderedMap(mappings, order, meta);
  }

}
