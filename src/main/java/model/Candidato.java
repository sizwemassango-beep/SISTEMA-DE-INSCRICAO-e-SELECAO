/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDate;

/**
 *
 * @author Sizwe Massango
 */
public class Candidato extends Utilizador {
  private  String numeroBi;
   private String provincia;
   private  String destrito;
   private String numeroCelular;
   private LocalDate dataNascimento;  
   private LocalDate dataInscricao= LocalDate.now(); // guarda  a data de hojje 

   
   
   
    public Candidato() {
    }
   
   
   
   
   

    public Candidato(String numeroBi, String provincia, String destrito, String numeroCelular,
            LocalDate dataNascimento, String nomeCompleto, String email, String senhaHash) {
        
        
        super(nomeCompleto, email, senhaHash);
        this.numeroBi = numeroBi;
        this.provincia = provincia;
        this.destrito = destrito;
        this.numeroCelular = numeroCelular;
        this.dataNascimento = dataNascimento;
    }
   
   
   
   
   
   
   
   
   
}
