package Objects;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static BD_Connect.UserDB.hash;

public class User {
	private int idUser;
	private String lastname;
	private String name;
	private String tel;
	private String address;
	private Panier panier;
	private String email;
	private String mdp;
	private String gender;
	private LocalDate birthday;
	private int age = Period.between(birthday, LocalDate.now()).getYears();
	private String zodiaque = calculateZodiacSign(this.birthday);
	
	public User(int idUser,
				String lastname,
				String name,
				String tel,
				String address,
				Panier panier,
				String email,
				String mdp,
				String gender,
				String birthday) {
		this.idUser = idUser;
		this.lastname = lastname;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.panier = panier;
		this.email = email;
		this.mdp = hash(mdp);
		this.gender = gender;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.birthday = LocalDate.parse(birthday, formatter);
	}

	public User(int idUser, String lastname, String name, String tel, String address, String email,
				String mdp,String gender, String birthday)  {
		this.idUser = idUser;
		this.lastname = lastname;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.email = email;
		this.mdp = hash(mdp);
		this.gender = gender;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.birthday = LocalDate.parse(birthday, formatter);
	}

	public static String calculateZodiacSign(LocalDate birthday) {
		int day = birthday.getDayOfMonth();
		Month month = birthday.getMonth();

		switch (month) {
			case JANUARY:
				return (day <= 19) ? "Capricorn " : "Aquarius";
			case FEBRUARY:
				return (day <= 18) ? "Aquarius" : "Pisces";
			case MARCH:
				return (day <= 20) ? "Pisces" : "Aries";
			case APRIL:
				return (day <= 19) ? "Aries" : "Taurus";
			case MAY:
				return (day <= 20) ? "Taurus" : "Gemini";
			case JUNE:
				return (day <= 20) ? "Gemini" : "Cancer";
			case JULY:
				return (day <= 22) ? "Cancer" : "Leo";
			case AUGUST:
				return (day <= 22) ? "Leo" : "Virgo";
			case SEPTEMBER:
				return (day <= 22) ? "Virgo" : "Libra";
			case OCTOBER:
				return (day <= 22) ? "Libra" : "Scorpio";
			case NOVEMBER:
				return (day <= 21) ? "Scorpio" : "Sagittarius";
			case DECEMBER:
				return (day <= 21) ? "Sagittarius" : "Capricorn";
			default:
				throw new IllegalArgumentException("Invalid month");
		}
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getZodiaque() {
		return zodiaque;
	}

	public void setZodiaque(String zodiaque) {
		this.zodiaque = zodiaque;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Panier getPanier() {
		return panier;
	}

	public void setPanier(Panier panier) {
		this.panier = panier;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}



	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", lastname=" + lastname + ", name=" + name + ", tel=" + tel + ", address="
				+ address + ", panier=" + panier + ", email=" + email + ", mdp=" + mdp + "]";
	}
	
}
