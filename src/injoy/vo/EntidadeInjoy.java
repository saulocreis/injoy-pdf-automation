package injoy.vo;

public class EntidadeInjoy extends VOAbstrato {
	
	private String nome;
	private String slug;
	
	public EntidadeInjoy(int id, String nome, String slug) {
		setId(id);
		setNome(nome);
		setSlug(slug);
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getSlug() {
		return slug;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setSlug(String slug) {
		this.slug = slug;
	}

}
