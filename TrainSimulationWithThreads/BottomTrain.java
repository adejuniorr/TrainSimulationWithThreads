import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class BottomTrain extends Thread {
  // Atributos:
  private Pane trainPane;
  private Path trainPath;
  private PathTransition trainMovement;

  // Construtor:
  public BottomTrain(Pane trainPane, Path trainPath) {
    this.trainPane = trainPane;
    this.trainPath = trainPath;
    this.trainMovement = createPathTransition(trainPane, trainPath);
    this.trainMovement.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
    this.trainPath.setStroke(Color.rgb(0, 0, 255, 1));
  }

  // Metodos:
  @Override
  public void run() {
    try {
      Platform.runLater(() -> {
        if (trainMovement != null) {

          playTrain();

          trainMovement.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if(Principal.lock1 != 0) {
              pauseTrain();
            } else if(Principal.t1Paused) {
              playTrain();
            }

            double yPosition = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinY();

            while(yPosition >= 192 && yPosition <= 244) {
              Principal.lock1 = 1;
              if (Principal.t2Paused) {
                playTrain();
              }

              yPosition++;
            }
            Principal.lock1 = 0;
            
            System.out.println("T2 pos: " + yPosition);
            //System.out.println("Lock1: " + Principal.lock1);
            //System.out.println("Lock2: " + Principal.lock2);
          });
        

          /* trainMovement.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if(Principal.lock != 0) {
              pauseTrain();
            } else {
              playTrain();
            }

            Duration currentTime = trainMovement.getCurrentTime();
            double msTime = currentTime.toMillis();

            System.out.println("Train1: " + msTime);
            
            if((msTime >= 54 && msTime <= 168) || (msTime >= 222 && msTime <= 338)) {
              Principal.lock = 1;
            } else {
              Principal.lock = 0;
            }
          }); */
        }
      });
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  // Getters e Setters:
  public Pane getTrain() {
    return trainPane;
  }

  public void setNewTrain(Pane newTrainPane) {
    this.trainPane = newTrainPane;
  }

  public Path getPath() {
    return trainPath;
  }

  public void setNewPath(Path newTrainPath) {
    this.trainPath = newTrainPath;
    this.trainMovement.setPath(newTrainPath);
  }

  public double getTrainSpeed() {
    return trainMovement.getRate();
  }

  public void setTrainSpeed(double speed) {
    this.trainMovement.setRate(speed);
  };

  public PathTransition getTrainMovement() {
    return trainMovement;
  }
  // FIM Getters e Setters

  // Metodos exclusivos do trem:

  /**
   * ***************************************************************
   * Metodo: playTrain
   * Funcao: reinicia a movimentacao dos trens apos um pause
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  public void playTrain() {
    trainMovement.play();
    Principal.t2Paused = false;
  }

  /**
   * ***************************************************************
   * Metodo: pauseTrain
   * Funcao: pausa a movimentacao dos trens
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  public void pauseTrain() {
    trainMovement.pause();
    Principal.t2Paused = true;
  }

  /**
   * ***************************************************************
   * Metodo: resetPos
   * Funcao: move os trens da posicao atual para a posicao inicial da animacao
   * Parametros: rebece um boolean
   * Retorno: nao retorna valor
   *************************************************************** 
   * @param isMoving flag para checar se os trens estao ou nao em movimento
   */
  public void resetPos(boolean isMoving) {
    if (isMoving) {
      // Para as animacoes dos trens 1 e 2 e os retorna para a respectiva posicao
      // inicial
      trainMovement.jumpTo(Duration.ZERO);
      trainMovement.playFromStart();
    } else {
      trainMovement.jumpTo(Duration.ZERO);
      trainMovement.stop();
    }
  }

  /**
   * ***************************************************************
   * Metodo: createPathTransition
   * Funcao: define a movimentacao do trem
   * Parametros: o trem (aqui criado a patir de um Pane) e o caminho do trem
   * (criado na funcao anterior - createPath)
   * Retorno: nao retorna valores
   ***************************************************************
   * @param trainPane painel que representa o trem
   * @param trainPath caminho que o trem vai percorrer
   * @return retorna a transicao/animacao completa
   */
  public PathTransition createPathTransition(Pane trainPane, Path trainPath) {
    PathTransition pathTransition = new PathTransition();
    pathTransition.setPath(trainPath); // O caminho onde a transicao/animacao ocorrera
    pathTransition.setNode(trainPane); // O no/elemento que vai se mover pelo caminho
    pathTransition.setInterpolator(Interpolator.LINEAR); // Matem a animacao linear (sem que os trens acelerem ou
                                                         // desacelerem no inicio ou fim)

    pathTransition.setRate(0.1); // Velocidade inicial dos trens (0.1 = 10 Km/h)
    pathTransition.setCycleCount(PathTransition.INDEFINITE); // Repeticao indefinida (ate que o usuario clique em reset)
    pathTransition.setAutoReverse(false); // Nao inverte a animacao (o trem volta pro inicio ao terminar o percurso)

    return pathTransition; // Retorna a animacao completa
  }
}