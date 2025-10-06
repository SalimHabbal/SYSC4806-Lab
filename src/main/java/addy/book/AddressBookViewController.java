package addy.book;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AddressBookViewController {

    private final AddressBookRepository addressBooks;

    public AddressBookViewController(AddressBookRepository addressBooks) {
        this.addressBooks = addressBooks;
    }

    @GetMapping("/addressbooks/{id}")
    public String viewAddressBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", addressBooks.findById(id).orElse(null));
        return "addressbook";
    }
}
