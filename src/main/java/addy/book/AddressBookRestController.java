package addy.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AddressBookRestController {

    private final AddressBookRepository addressBooks;
    private final BuddyInfoRepository buddies;

    public AddressBookRestController(AddressBookRepository addressBooks, BuddyInfoRepository buddies) {
        this.addressBooks = addressBooks;
        this.buddies = buddies;
    }

    @PostMapping("/addressbooks")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressBook createAddressBook() {
        return addressBooks.save(new AddressBook());
    }

    @GetMapping("/addressbooks/{id}")
    public AddressBook getAddressBook(@PathVariable Long id) {
        return addressBooks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AddressBook not found"));
    }

    @PostMapping("/addressbooks/{id}/buddies")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressBook addBuddy(@PathVariable Long id, @RequestBody Map<String, String> body) {
        AddressBook book = addressBooks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AddressBook not found"));

        String name = body.get("name");
        String phone = body.get("phone");
        String address = body.get("address");

        if (name == null || phone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name and phone are required");
        }

        BuddyInfo saved = buddies.save(new BuddyInfo(name, phone, address));
        book.addBuddy(saved);
        return addressBooks.save(book);
    }

    @DeleteMapping("/addressbooks/{id}/buddies/{buddyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBuddy(@PathVariable Long id, @PathVariable Long buddyId) {
        AddressBook book = addressBooks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AddressBook not found"));
        BuddyInfo buddy = buddies.findById(buddyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buddy not found"));

        if (!book.getBuddies().removeIf(b -> b.getId().equals(buddyId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Buddy not in this AddressBook");
        }
        addressBooks.save(book);
        buddies.delete(buddy);
    }

    @GetMapping("/buddies")
    public Iterable<BuddyInfo> findBuddies(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String phone) {
        if (name != null) return buddies.findByName(name);
        if (phone != null) return buddies.findByPhone(phone);
        return buddies.findAll();
    }
}
