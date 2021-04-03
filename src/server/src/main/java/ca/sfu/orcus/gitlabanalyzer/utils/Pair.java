package ca.sfu.orcus.gitlabanalyzer.utils;

public class Pair<U, V> {
    public U firstAttribute;
    public V secondAttribute;

    public Pair(U firstAttribute, V secondAttribute) {
        this.firstAttribute = firstAttribute;
        this.secondAttribute = secondAttribute;
    }
}
