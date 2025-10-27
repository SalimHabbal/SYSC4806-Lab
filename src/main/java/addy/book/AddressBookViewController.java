package addy.book;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class AddressBookViewController {

    private final AddressBookRepository addressBooks;
    private final BuddyInfoRepository buddies;

    // Lab6: Inject both address books and buddy repositories to support HTML form interactions.
    public AddressBookViewController(AddressBookRepository addressBooks, BuddyInfoRepository buddies) {
        this.addressBooks = addressBooks;
        this.buddies = buddies;
    }

    @GetMapping({"/", "/addressbooks"})
    public String listAddressBooks(Model model) {
        // Lab6: Render a home/listing page showing all address books in ID order.
        List<AddressBook> books = StreamSupport.stream(addressBooks.findAll().spliterator(), false)
                .sorted(Comparator.comparing(AddressBook::getId))
                .collect(Collectors.toList());
        model.addAttribute("books", books);
        return "addressbooks";
    }

    @GetMapping("/addressbooks/{id}")
    public String viewAddressBook(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Lab6: Gracefully handle missing books and redirect with an error flash message.
        AddressBook book = addressBooks.findById(id).orElse(null);
        if (book == null) {
            redirectAttributes.addFlashAttribute("listError", "Address book " + id + " was not found.");
            return "redirect:/addressbooks";
        }
        model.addAttribute("book", book);
        return "addressbook";
    }

    @PostMapping("/addressbooks")
    public String createAddressBook(RedirectAttributes redirectAttributes) {
        // Lab6: Create a new address book and jump directly to its detail view with confirmation.
        AddressBook book = addressBooks.save(new AddressBook());
        redirectAttributes.addFlashAttribute("message", "Created address book #" + book.getId());
        return "redirect:/addressbooks/" + book.getId();
    }

    @PostMapping("/addressbooks/{id}/buddies")
    public String addBuddy(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam String phone,
                           @RequestParam(required = false) String address,
                           RedirectAttributes redirectAttributes) {
        // Lab6: Process buddy form submissions, validating required fields and surfacing flash feedback.
        AddressBook book = addressBooks.findById(id).orElse(null);
        if (book == null) {
            redirectAttributes.addFlashAttribute("listError", "Address book " + id + " was not found.");
            return "redirect:/addressbooks";
        }
        String trimmedName = name == null ? "" : name.trim();
        String trimmedPhone = phone == null ? "" : phone.trim();
        String trimmedAddress = address == null ? null : address.trim();
        if (trimmedAddress != null && trimmedAddress.isEmpty()) {
            trimmedAddress = null;
        }

        if (trimmedName.isEmpty() || trimmedPhone.isEmpty()) {
            redirectAttributes.addFlashAttribute("formError", "Name and phone are required.");
            return "redirect:/addressbooks/" + id;
        }

        BuddyInfo buddy = buddies.save(new BuddyInfo(trimmedName, trimmedPhone, trimmedAddress));
        book.addBuddy(buddy);
        addressBooks.save(book);
        redirectAttributes.addFlashAttribute("formSuccess", "Added " + trimmedName + " to address book.");
        return "redirect:/addressbooks/" + id;
    }
}
