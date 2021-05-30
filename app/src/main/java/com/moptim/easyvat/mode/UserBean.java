package com.moptim.easyvat.mode;

public class UserBean {

    private String number;
    private String name;
    private int age;
    private String idCard;  //身份证
    private String gender;  //性别
    private String school;  //学校
    private String mGrade;  //年纪
    private String mClass;  //班级

    public UserBean(){
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String mGrade) {
        this.mGrade = mGrade;
    }

    public String getUserClass() {
        return mClass;
    }

    public void setUserClass(String mClass) {
        this.mClass = mClass;
    }

    @Override
    public String toString() {
        return number + ", " +
                name + ", " +
                age + ", " +
                idCard + ", " +
                gender + ", " +
                school + ", " +
                mGrade + ", " +
                mClass;
    }
}
