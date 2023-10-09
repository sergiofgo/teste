package br.nom.belo.marcio.simuladorvoo;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Aviao implements Runnable {
  private String teste;
  private static final Logger LOG = LoggerFactory.getLogger("Aviao");
  private Aeroporto aeroporto;
  private String idAviao;
  private long tempoVoo = 0;

  public Aviao(Aeroporto aeroporto, String idAviao, long tempoVoo) {
    this.aeroporto = aeroporto;
    this.idAviao = idAviao;
    this.tempoVoo = tempoVoo;
  }

  public void run() {
    try {
      Thread.sleep(tempoVoo / 2);
    } catch (InterruptedException ie) {
      Util.logarEReinterromper();
    }

    decolar();
    voar();
    aterrisar();
    LOG.info("{} em solo.", idAviao);
  }

  private void decolar() {
    LOG.info("{} pedindo autorização ao {} para decolar...", idAviao, aeroporto.getNomeAeroporto());
    aeroporto.esperarPistaDisponivel(idAviao); // Espera uma pista livre
    LOG.info("{} decolando...", idAviao);
  }

  private void voar() {
    LOG.info("{} voando...", idAviao);
    try {
      Thread.sleep(tempoVoo);
    } catch (InterruptedException e) {
      Util.logarEReinterromper();
    }
  }

  private void aterrisar() {
    LOG.info("{} pedindo autorização ao {} para aterrisar...", idAviao, aeroporto.getNomeAeroporto());
    aeroporto.esperarPistaDisponivel(idAviao); // Espera uma pista livre
    LOG.info("{} aterrisando...", idAviao);
  }
}

class Aeroporto implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger("Aeroporto");
  private boolean temPistaDisponivel = true;
  private String nomeAeroporto;
  private Random random = new Random();

  public Aeroporto(String nomeAeroporto) {
    this.nomeAeroporto = nomeAeroporto;
  }

  public String getNomeAeroporto() {
    return nomeAeroporto;
  }

  public synchronized void esperarPistaDisponivel(String idAviao) {
    if (!temPistaDisponivel) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        Util.logarEReinterromper();
      }
    }

    LOG.info("{} autoriza {} para utilizar pista", nomeAeroporto, idAviao);
    temPistaDisponivel = false;
  }

  public synchronized void mudarEstadoPistaDisponivel() {
    // Inverte o estado da pista.
    temPistaDisponivel = !temPistaDisponivel;
    LOG.info("{} tem pista disponível? {}", nomeAeroporto, (temPistaDisponivel ? "Sim" : "Não"));

    // Notifica a mudanca de estado para quem estiver esperando.
    if (temPistaDisponivel) {
      this.notifyAll();
    }
  }

  public void run() {
    LOG.info("Rodando aeroporto {}", nomeAeroporto);
    do {
      try {
        mudarEstadoPistaDisponivel();
        // Coloca a thread aeroporto dormindo por um tempo de 0 a 5s
        Thread.sleep(random.nextInt(5000));
      } catch (InterruptedException e) {
        Util.logarEReinterromper();
      }
    } while (Thread.activeCount() > 2); // NOSONAR
  }
}

public final class SimuladorVoo {
  private static final Logger LOG = LoggerFactory.getLogger("SimuladorVoo");
  private static Random random = new Random();

  public static void main(String[] args) {
    LOG.info("Rodando simulador de voo.");

    // Constroi aeroporto e inicia sua execucao - NÃO MEXER NESSE TRECHO
    Aeroporto santosDumont = new Aeroporto("Santos Dumont");
    Thread threadAeroporto = new Thread(santosDumont, "santosDumont");

    // Constrói aviao e inicia sua execucao - NÃO MEXER NESSE TRECHO
    Aviao aviao14bis = new Aviao(santosDumont, "Avião 14BIS", 10000);
    Thread thread14bis = new Thread(aviao14bis, "aviao14bis");

    Aviao aviaoTecoTeco = new Aviao(santosDumont, "Avião Teco Teco", SimuladorVoo.random.nextInt(10000));
    Thread threadTecoTeco = new Thread(aviaoTecoTeco, "aviaoTecoTeco");

    Aviao aviaoBoeing747 = new Aviao(santosDumont, "Avião Boeing 747", SimuladorVoo.random.nextInt(10000));
    Thread threadBoeing747 = new Thread(aviaoBoeing747, "aviaoBoeing747");

    Aviao aviaoAirbus380 = new Aviao(santosDumont, "Avião Airbus 380", SimuladorVoo.random.nextInt(10000));
    Thread threadAirbus380 = new Thread(aviaoAirbus380, "aviaoAirbus380");

    Aviao aviaoConcorde = new Aviao(santosDumont, "Avião Concorde", SimuladorVoo.random.nextInt(10000));
    Thread threadConcorde = new Thread(aviaoConcorde, "aviaoConcorde");

    // Inicia as threads
    threadAeroporto.start();
    thread14bis.start();
    threadTecoTeco.start();
    threadBoeing747.start();
    threadAirbus380.start();
    threadConcorde.start();

    try {
      // Junta-se ao término da execução da thread do aeroporto
      threadAeroporto.join();
    } catch (InterruptedException ex) {
      Util.logarEReinterromper();
    }

    LOG.info("Terminando thread principal.");
  }
}

class Util {
  private static final Logger LOG = LoggerFactory.getLogger("Util");

  private Util() {
  }

  static void logarEReinterromper() {
    LOG.error("Thread interrompida");
    Thread.currentThread().interrupt();
  }
}