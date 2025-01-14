package Objects;

public class User {
	private int idUser;
	private String lastname;
	private String name;
	private String tel;
	private String address;
	private Panier panier;
	private String email;
	private String mdp;
	
	public User(int idUser, String lastname, String name, String tel, String address, Panier panier, String email,
			String mdp) {
		this.idUser = idUser;
		this.lastname = lastname;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.panier = panier;
		this.email = email;
		this.mdp = mdp;
	}

	public User(int idUser, String lastname, String name, String tel, String address, String email,
				String mdp) {
		this.idUser = idUser;
		this.lastname = lastname;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.email = email;
		this.mdp = mdp;
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
