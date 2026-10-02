package org.silentsos.model;

/**
 * Represents an emergency contact person.
 * Demonstrates ENCAPSULATION: private members guarded by public getters,
 * setters, and validation methods.
 */
public class Contact {
    private String name;
    private String phone;
    private String email;
    private int priority; // 1 = Primary, 2 = Secondary, etc.

    public Contact(String name, String phone, String email, int priority) {
        setName(name);
        setPhone(phone);
        setEmail(email);
        setPriority(priority);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        this.phone = phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        this.email = email.trim();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < 1) {
            this.priority = 1;
        } else {
            this.priority = priority;
        }
    }

    @Override
    public String toString() {
        return String.format("[Priority %d] %s | Phone: %s | Email: %s", priority, name, phone, email);
    }
}
