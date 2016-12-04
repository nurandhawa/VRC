package ca.sfu.teambeta.notifications;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

import ca.sfu.teambeta.persistence.DBManager;

/**
 * Facilitates the creation, modification and deletion of Announcements, which are displayed
 * to the user upon logging in to the system.
 */
public class AnnouncementManager {
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Announcement> announcementList;

    private DBManager dbManager;

    public AnnouncementManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.announcementList = new ArrayList<>();
        this.announcementList.addAll(dbManager.getAnnouncements());
    }

    public void addAnnouncement(Announcement announcement) {
        announcementList.add(announcement);
        dbManager.persistEntity(announcement);
    }

    public boolean removeAnnouncement(int id) {
        return announcementList.removeIf(announcement -> announcement.getID() == id);
    }

    /**
     * This method maintains the announcementList by refreshing it to contain only the list of
     * valid (unexpired) announcements, before returning the refreshed list to the caller.
     */
    private List<Announcement> getValidAnnouncements() {
        Date now = new Date();
        announcementList.removeIf(announcement -> announcement.getExpiryDate() != null && announcement.getExpiryDate().after(now));
        return announcementList;
    }

    public String getAnnouncementsJSON() {
        List<Announcement> announcements = getValidAnnouncements();
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(announcements);
    }

    public int getAnnouncementCount() {
        return getValidAnnouncements().size();
    }

    public boolean editAnnouncement(int id, Announcement editedAnnouncement) {
        boolean removedExistingAnnouncement = removeAnnouncement(id);
        if (removedExistingAnnouncement) {
            addAnnouncement(editedAnnouncement);
            return true;
        }
        return false;
    }
}
