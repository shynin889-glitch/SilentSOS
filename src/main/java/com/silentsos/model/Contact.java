package com.silentsos.model;

import java.io.Serializable;

/**
 * Model representing a trusted emergency contact or guardian.
 */
public class Contact implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String phone;
    private String email;
    private String relationship;
    private boolean primary;

    public Contact() {}

    public Contact(String id, String name, String phone, String email, String relationship, boolean primary) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relationship = relationship;
        this.primary = primary;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", relationship='" + relationship + '\'' +
                ", primary=" + primary +
                '}';
    }
}
