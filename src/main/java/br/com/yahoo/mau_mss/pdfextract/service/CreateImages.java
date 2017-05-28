package br.com.yahoo.mau_mss.pdfextract.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentCatalog;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: GeraHtm</p>
 * <p>Description:  </p>
 * <p>Date: Jun 20, 2012, 12:38:39 PM</p>
 * @author Mauricio Soares da Silva (mauricio.soares)
 */
public class CreateImages {
  private int tamanhoEspecifico;
  private int atualGeral;
  private int atualEspecifico;
  private boolean feito;
  private boolean cancelado;
  private String mensagemGeral;
  private String mensagemEspecifico;
  private boolean sucesso;
  private File inputFile;
  private GeraHtmTask geraHtmTask;
  private TaskException taskException;
  private String errorMessage;
  public static final int ONE_SECOND = 1000;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Create a new instance of <code>GeraHtm</code>.
   */
  public CreateImages(File inputFile) {
    this.inputFile = inputFile;
  }
  
  public void go() {
    this.tamanhoEspecifico = 0;
    this.feito = false;
    this.cancelado = false;
    (this.geraHtmTask = new GeraHtmTask()).execute();
  }

  public void stop() {
    this.cancelado = true;
    this.mensagemGeral = null;
    this.mensagemEspecifico = null;
    this.geraHtmTask.cancel(true);
    this.geraHtmTask = null;
  }
  
  public int getTamanhoEspecifico() {
      return this.tamanhoEspecifico;
  }
  
  public int getAtualGeral() {
      return this.atualGeral;
  }
  
  public int getAtualEspecifico() {
      return this.atualEspecifico;
  }
  
  public boolean estaFeito() {
      return this.feito;
  }
  
  public boolean isSucesso() {
    return this.sucesso;
  }
  
  public String getMensagemGeral() {
      return this.mensagemGeral;
  }
  
  public String getMensagemEspecifico() {
      return this.mensagemEspecifico;
  }
  
  public String getErrorMessage() {
    if (this.taskException != null)
      this.errorMessage = this.taskException.process;
    return this.errorMessage;
  }
  
  private static class TaskFeedBack {
    /** posição atual geral */
    private final int masterPos;
    /** posição atual específica */
    private final int detailPos;
    /** tamanho máximo específico */
    private final int detailLength;
    /** mensagem atual geral */
    private final String masterMsg;
    /** mensagem atual específica */
    private final String detailMsg;

    TaskFeedBack(int masterPos, int detailPos, int detailLength, String masterMsg, String detailMsg) {
      this.masterPos = masterPos;
      this.detailPos = detailPos;
      this.detailLength = detailLength;
      this.masterMsg = masterMsg;
      this.detailMsg = detailMsg;
    }
  }
  
  private static class TaskException {
    private final String process;

    TaskException(String process) {
      this.process = process;
    }
  }
  
  private class GeraHtmTask extends SwingWorker<TaskException, TaskFeedBack> {
      List<String> saida = new ArrayList<String>();
      String outputFolder;

      @Override
      protected TaskException doInBackground() throws Exception {
        int posEspecifico = 0, maxEspecifico = 4;
        
        try {
            Thread.sleep(ONE_SECOND); //pára por um segundo para verificar o try anterior
            try {
                if (! this.isCancelled()) {
                  this.publish(new TaskFeedBack(0, posEspecifico, maxEspecifico,
                                              null, "Criando diretório de destino..."));
                  if (inputFile == null)
                    throw new IllegalArgumentException("Arquivo origem indeterminado");
                  outputFolder = StringUtils.substringBeforeLast(inputFile.getPath(), SystemUtils.FILE_SEPARATOR) +
                                 SystemUtils.FILE_SEPARATOR + 
                                 StringUtils.substringBeforeLast(inputFile.getName(), ".");
                  System.out.println("=========================================");
                  System.out.println("outputFolder= " + outputFolder);
                  File output = new File(outputFolder);
                  if (! output.exists())
                    output.mkdir();
                  posEspecifico++;
                  Thread.sleep(ONE_SECOND); // para por um segundo para devolver as mensagens gerais
                }
                if (! this.isCancelled()) {
                  this.publish(new TaskFeedBack(0, posEspecifico, maxEspecifico,
                                              null, "Buscando imagens..."));
                  Thread.sleep(ONE_SECOND); // para por um segundo para devolver as mensagens gerais
                  readPdf();
                  posEspecifico++;
                  try {
                    Validate.notEmpty(saida);
                  }catch(RuntimeException re) {
                    return new TaskException("Nenhum arquivo encontrado");
                  }
                  logger.info("Número de linhas salva: " + saida.size());
                }
            } catch(Exception e) {
              logger.error("Não foi possível concluir o backup", e);
              return new TaskException("Não foi possível concluir o backup");
            } // end catch
        } catch (InterruptedException e) {
          logger.error("TarefaAtual interrompida.", e);
        } // end catch
        return null;
      }

      /**
      * Feedback para a tela de progresso da operação
      * @param feeds
      */
      @Override
      protected void process(List<TaskFeedBack> feeds) {
        TaskFeedBack feed = feeds.get(feeds.size() - 1);
        atualGeral = feed.masterPos;
        atualEspecifico = feed.detailPos;
        tamanhoEspecifico = feed.detailLength;
        mensagemGeral = feed.masterMsg;
        mensagemEspecifico = feed.detailMsg;
      }

      /**
      * Finalização do processo de leitura após terminada a execução do
      * método doInBackground
      */
      @Override
      protected void done() {
        try {
          taskException = this.get();
        } catch(InterruptedException ignore) {}
        catch(ExecutionException ee) {
          String why;
          Throwable cause = ee.getCause();
          if (cause != null)
            why = cause.getMessage();
          else
            why = ee.getMessage();
          logger.error("Erro ao executar tarefa: " + why, ee);
        }
        //atualGeral = tamanhoGeral;
        feito = true;
        mensagemGeral = "Tarefa completada";
        mensagemEspecifico = "";
      }
      
      @SuppressWarnings("unchecked")
      private void readPdf() throws IOException {
        PDDocument document = null; 
        try {
          document = PDDocument.load(inputFile.getPath());
          if (document.getDocumentCatalog() == null) {
            System.out.println("Não foi possível buscar o catálogo do pdf.");
            return;
          }
          List<PDPage> pages = document.getDocumentCatalog().getAllPages();
          Iterator<PDPage> iter = pages.iterator(); 
          int i = 1;
          while (iter.hasNext()) {
              PDPage page = iter.next();
              PDResources resources = page.getResources();
              Map<String, PDXObjectImage> pageImages = resources.getImages();
              if (pageImages != null) { 
                  Iterator<String> imageIter = pageImages.keySet().iterator();
                  while (imageIter.hasNext()) {
                      String key = imageIter.next();
                      PDXObjectImage image = pageImages.get(key);
                      if (image != null)
                        saida.add("image" + i);
                      image.write2file(outputFolder + SystemUtils.FILE_SEPARATOR + 
                              "image" + StringUtils.leftPad(String.valueOf(i), 3, "0"));
                      i ++;
                  }
              }
          }
        } finally {
          try {
            document.close();
          }catch(Exception ignore){}
        }
      }
     
  }

}
