package ca.sfu.teambeta.notifications;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import org.hibernate.annotations.Type;

import java.util.Date;

import javax.persistence.Entity;

import ca.sfu.teambeta.persistence.Persistable;

/**
 * Data structure holding an Announcement.
 *
 * @see AnnouncementManager
 */
@Entity(name = "Announcement")
public class Announcement extends Persistable {
    @Expose
    @Type(type = "text")
    private String title;
    @Expose
    @Type(type = "text")
    private String message;

    @Type(type = "timestamp")
    private Date createdDate;

    @Type(type = "timestamp")
    private Date expiryDate;

    public Announcement(String title, String message) {
        this(title, message, null);
    }

    public Announcement(String title, String message, Date expiryDate) {
        this.title = title;
        this.message = message;
        this.expiryDate = expiryDate;

        this.createdDate = new Date();
    }

    public Announcement() {
    }

    public String getAnnouncementJSON() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Announcement) {
            Announcement announcement = (Announcement) o;
            return this.getID() == announcement.getID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getID();
    }
}
