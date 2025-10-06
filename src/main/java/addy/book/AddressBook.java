package addy.book;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AddressBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "addressbook_id")
    private List<BuddyInfo> buddies = new ArrayList<>();

    public AddressBook() {}

    public Long getId() { return id; }

    public List<BuddyInfo> getBuddies() { return buddies; }

    public void addBuddy(BuddyInfo buddy) {
        if (buddy != null) buddies.add(buddy);
    }

    public boolean removeBuddy(BuddyInfo buddy) {
        return buddies.remove(buddy);
    }
}
