package ca.sfu.teambeta.persistence;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Persistable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getID() {
        return id;
    }

    // Keep this setter for the purpose of comparing IDs during tests
    protected void setID(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Persistable object = (Persistable) o;
        return id == object.id;
    }

    @Override
    // Once we get a database setup, the id attribute will be guaranteed unique
    public int hashCode() {
        return id;
    }
}
