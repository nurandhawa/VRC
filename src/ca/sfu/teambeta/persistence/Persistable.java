package ca.sfu.teambeta.persistence;

import com.google.gson.annotations.Expose;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Persistable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    public int getID() {
        return id;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
