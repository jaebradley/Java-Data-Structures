package data.structures.lists.interfaces;

import data.structures.lists.impl.SimpleHashRing;

import java.util.Optional;

public interface HashRing<Key, Value> {
    class NodeAlreadyExists extends Exception {
    }

    class AtLeastOneNodeMustExist extends Exception {
    }

    class EntryDoesNotExist extends Exception {
    }

    void addNode(SimpleHashRing.Position location) throws NodeAlreadyExists;

    void removeNode(SimpleHashRing.Position location) throws AtLeastOneNodeMustExist;

    Optional<Value> addEntry(Key key, Value value);

    Value removeEntry(Key key) throws EntryDoesNotExist;

    Optional<Value> getValue(Key key);
}
