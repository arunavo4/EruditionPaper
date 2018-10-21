package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Avatar")
    @Expose
    private String avatar;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("RegDate")
    @Expose
    private String regDate;
    @SerializedName("SecretCode")
    @Expose
    private String secretCode;
    @SerializedName("EId")
    @Expose
    private String eId;
    @SerializedName("PersonCollege")
    @Expose
    private List<PersonCollege> personCollege = null;
    @SerializedName("PersonAcademic")
    @Expose
    private List<Object> personAcademic = null;
    @SerializedName("PersonAddress")
    @Expose
    private List<Object> personAddress = null;
    @SerializedName("PersonFamily")
    @Expose
    private List<Object> personFamily = null;
    @SerializedName("PersonCourse")
    @Expose
    private List<Object> personCourse = null;
    @SerializedName("PersonFile")
    @Expose
    private List<Object> personFile = null;
    @SerializedName("PersonForm")
    @Expose
    private List<Object> personForm = null;
    @SerializedName("Role")
    @Expose
    private String role;
    @SerializedName("Status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getEId() {
        return eId;
    }

    public void setEId(String eId) {
        this.eId = eId;
    }

    public List<PersonCollege> getPersonCollege() {
        return personCollege;
    }

    public void setPersonCollege(List<PersonCollege> personCollege) {
        this.personCollege = personCollege;
    }

    public List<Object> getPersonAcademic() {
        return personAcademic;
    }

    public void setPersonAcademic(List<Object> personAcademic) {
        this.personAcademic = personAcademic;
    }

    public List<Object> getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(List<Object> personAddress) {
        this.personAddress = personAddress;
    }

    public List<Object> getPersonFamily() {
        return personFamily;
    }

    public void setPersonFamily(List<Object> personFamily) {
        this.personFamily = personFamily;
    }

    public List<Object> getPersonCourse() {
        return personCourse;
    }

    public void setPersonCourse(List<Object> personCourse) {
        this.personCourse = personCourse;
    }

    public List<Object> getPersonFile() {
        return personFile;
    }

    public void setPersonFile(List<Object> personFile) {
        this.personFile = personFile;
    }

    public List<Object> getPersonForm() {
        return personForm;
    }

    public void setPersonForm(List<Object> personForm) {
        this.personForm = personForm;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
