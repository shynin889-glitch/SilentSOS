package silentsos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class EmergencyContactController {

    private final EmergencyContactRepository contactRepository;
    private final UserRepository userRepository;

    public EmergencyContactController(
            EmergencyContactRepository contactRepository,
            UserRepository userRepository) {

        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    // Add emergency contact
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addContact(
            @PathVariable Long userId,
            @RequestBody EmergencyContact contact) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        contact.setUser(user);

        EmergencyContact savedContact =
                contactRepository.save(contact);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedContact);
    }

    // View all contacts of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getContacts(
            @PathVariable Long userId) {

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        List<EmergencyContact> contacts =
                contactRepository.findByUserId(userId);

        return ResponseEntity.ok(contacts);
    }

    // Edit contact
    @PutMapping("/{contactId}")
    public ResponseEntity<?> updateContact(
            @PathVariable Long contactId,
            @RequestBody EmergencyContact updatedContact) {

        EmergencyContact contact =
                contactRepository.findById(contactId).orElse(null);

        if (contact == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Contact not found");
        }

        contact.setName(updatedContact.getName());
        contact.setPhone(updatedContact.getPhone());
        contact.setRelationship(updatedContact.getRelationship());
        contact.setEmail(updatedContact.getEmail());

        EmergencyContact savedContact =
                contactRepository.save(contact);

        return ResponseEntity.ok(savedContact);
    }

    // Delete contact
    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(
            @PathVariable Long contactId) {

        if (!contactRepository.existsById(contactId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Contact not found");
        }

        contactRepository.deleteById(contactId);

        return ResponseEntity.ok("Contact deleted successfully");
    }
}