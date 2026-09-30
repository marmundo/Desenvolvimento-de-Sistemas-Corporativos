package br.edu.ifrn.petshop.model;

public class Pet {
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private Integer idade;

    public Pet(Long id, String nome, String especie, String raca, Integer idade) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
}
