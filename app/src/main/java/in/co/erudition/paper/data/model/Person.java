package in.co.erudition.paper.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person {
    @SerializedName("EId")
    @Expose
    private String eId;
    @SerializedName("Avatar")
    @Expose
    private String avatar;
    @SerializedName("ProfileImage")
    @Expose
    private String profileImage;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("MiddleName")
    @Expose
    private String middleName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("Role")
    @Expose
    private String role;
    @SerializedName("DOB")
    @Expose
    private String dOB;
    @SerializedName("Gender")
    @Expose
    private String gender;
    @SerializedName("RegDate")
    @Expose
    private String regDate;
    @SerializedName("SecretCode")
    @Expose
    private String secretCode;
    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("Religion")
    @Expose
    private String religion;
    @SerializedName("BoardCode")
    @Expose
    private String boardCode;
    @SerializedName("CourseCode")
    @Expose
    private String courseCode;
    @SerializedName("SessionCode")
    @Expose
    private String sessionCode;
    @SerializedName("SubjectCode")
    @Expose
    private String subjectCode;
    @SerializedName("CollegeCode")
    @Expose
    private String collegeCode;
    @SerializedName("PreviousSession")
    @Expose
    private String previousSession;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("BoardName")
    @Expose
    private String boardName;
    @SerializedName("BoardFullName")
    @Expose
    private String boardFullName;
    @SerializedName("CourseName")
    @Expose
    private String courseName;
    @SerializedName("CourseFullName")
    @Expose
    private String courseFullName;
    @SerializedName("SessionName")
    @Expose
    private String sessionName;
    @SerializedName("SessionFullName")
    @Expose
    private String sessionFullName;
    @SerializedName("Website")
    @Expose
    private String website;
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
    @SerializedName("PersonBoardNotification")
    @Expose
    private List<PersonBoardNotification> personBoardNotification = null;

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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
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

    public List<PersonBoardNotification> getPersonBoardNotification() {
        return personBoardNotification;
    }

    public void setPersonBoardNotification(List<PersonBoardNotification> personBoardNotification) {
        this.personBoardNotification = personBoardNotification;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getdOB() {
        return dOB;
    }

    public void setdOB(String dOB) {
        this.dOB = dOB;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPreviousSession() {
        return previousSession;
    }

    public void setPreviousSession(String previousSession) {
        this.previousSession = previousSession;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardFullName() {
        return boardFullName;
    }

    public void setBoardFullName(String boardFullName) {
        this.boardFullName = boardFullName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseFullName() {
        return courseFullName;
    }

    public void setCourseFullName(String courseFullName) {
        this.courseFullName = courseFullName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionFullName() {
        return sessionFullName;
    }

    public void setSessionFullName(String sessionFullName) {
        this.sessionFullName = sessionFullName;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }
}
