package top.evalexp.tools.common.util;

public class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K key() {
        return this.key;
    }

    public V value() {
        return this.value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
