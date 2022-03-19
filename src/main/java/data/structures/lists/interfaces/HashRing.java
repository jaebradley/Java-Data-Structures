package data.structures.lists.interfaces;

import data.structures.lists.impl.SimpleHashRing;

import java.util.Optional;

public interface HashRing<Key, Value> {
    class AtLeastOneNodeMustExist extends Exception {
    }

    void addNode(SimpleHashRing.Position position);

    void removeNode(SimpleHashRing.Position position) throws AtLeastOneNodeMustExist;

    Optional<Value> addEntry(Key key, Value value);

    Optional<Value> removeEntry(Key key);

    Optional<Value> getValue(Key key);
}
