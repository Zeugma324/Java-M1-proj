package Objects;

import java.time.*;
import java.time.format.DateTimeFormatter;


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
	private String birthday;
	private Integer age;
	private String zodiaque;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	LocalDate date_naissance;


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
		this.mdp = mdp;
		this.gender = gender;
		this.birthday = birthday;

			if (this.birthday != null && !this.birthday.isEmpty()) {
			this.date_naissance = LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.zodiaque = calculateZodiacSign(this.birthday);
		}
	}

	public User(int idUser,
				String lastname,
				String name,
				String tel,
				String address,
				String email,
				String mdp,
				String gender,
				String birthday) {
		this.idUser = idUser;
		this.lastname = lastname;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.email = email;
		this.mdp = mdp;
		this.gender = gender;
		this.birthday = birthday;

		if (this.birthday != null && !this.birthday.isEmpty()) {
			this.date_naissance = LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.zodiaque = calculateZodiacSign(this.birthday);
		}
	}

	public static String calculateZodiacSign(String birthday) {
		if (birthday == null || birthday.isEmpty()) {
			return null; // 或者返回一个默认值
		}
		LocalDate date_naissance = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		int day = date_naissance.getDayOfMonth();
		Month month = date_naissance.getMonth();

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
		if (age == null && this.date_naissance != null) {
			this.age = Period.between(this.date_naissance, LocalDate.now()).getYears();
		}
		return age == null ? 0 : age;
	}


	public void setAge(int age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
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
				+ address + ", email=" + email + "]";
	}
	
}
