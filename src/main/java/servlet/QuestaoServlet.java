package servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.bean.Avaliador;
import model.bean.Privado;
import model.bean.Questao;
import model.bean.QuestaoEntrada;
import model.bean.QuestaoImagem;
import model.bean.QuestaoRestricao;
import model.bean.QuestaoSaidaEsperada;
import model.dao.GenericDAO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import util.JDoodle;
import util.JDoodleOutputFormat;

/**
 *
 * @author eddunic
 */
public class QuestaoServlet extends HttpServlet {

    private String entradaFormat;
    private String saidaFormat;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = null;
        String enunciado = null;
        String entrada = null;
        String saida = null;
        String restricao = null;
        String entradaExemplo = null;
        String nivel = null;
        String peso = null;

        String compilerId = null;

        String value = null;

        //Read archive
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        File uploadedFile = null;
        File imagem = null;

        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();

                    if (!item.isFormField()) {
                        String fileName = item.getName();
                        File path = new File("D:\\home\\eddunic\\NetBeansProjects\\djudge");
                        if (!path.exists()) {
                            boolean status = path.mkdirs();
                        }
                        String name = item.getFieldName();
                        if (name.equals("imagem")) {
                            imagem = new File(path + "/" + fileName);
                            System.out.println(imagem.getAbsolutePath());
                            item.write(imagem);
                        } else {
                            uploadedFile = new File(path + "/" + fileName);
                            System.out.println(uploadedFile.getAbsolutePath());
                            item.write(uploadedFile);
                        }
                    } else {
                        String name = item.getFieldName();
                        value = item.getString();
                        if (name.equals("compilerId")) {
                            compilerId = value;
                        }
                        if (name.equals("titulo")) {
                            titulo = value;
                        }
                        if (name.equals("enunciado")) {
                            enunciado = value;
                        }
                        if (name.equals("entrada")) {
                            entrada = value;
                        }
                        if (name.equals("saida")) {
                            saida = value;
                        }
                        if (name.equals("restricao")) {
                            restricao = value;
                        }
                        if (name.equals("entradaExemplo")) {
                            entradaExemplo = value;
                        }
                        if (name.equals("nivel")) {
                            nivel = value;
                        }
                        if (name.equals("peso")) {
                            peso = value;
                        }

                        // **************************************************
                        // Process your name and value pairs here! *****
                        // **************************************************
                        System.out.println("Found field " + name + " and value " + value);
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //questao
        Questao q = new Questao();

        q.setTitulo(titulo.getBytes());
        q.setEnunciado(enunciado.getBytes());
        q.setEntrada(entrada.getBytes());
        q.setSaida(saida.getBytes());
        q.setNivel(Integer.parseInt(nivel));
        q.setPeso(Integer.parseInt(peso));

        Privado p = (Privado) request.getSession().getAttribute("usuario");

        q.setAvaliador((Avaliador) p);

        q.setIdAvaliador(q.getAvaliador().getId());

        GenericDAO<Questao> gq = new GenericDAO<>();
        gq.saveOrUpdate(q);
        //imagem
        byte[] fileContent = FileUtils.readFileToByteArray(new File(imagem.getAbsolutePath()));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        String ext[] = imagem.getAbsolutePath().split("\\.");
        int i = ext.length;
        String extensao = null;
        if (i > 1) {
            extensao = ext[i - 1];
        }

        String img = "data:image/" + extensao + ";base64," + encodedString;

        System.out.println(img);
        QuestaoImagem qi = new QuestaoImagem();

        qi.setImagem(img.getBytes());
        qi.setIdQuestao(String.valueOf(q.getId()));
        qi.setQuestao(q);

        GenericDAO<QuestaoImagem> gqi = new GenericDAO<>();
        gqi.saveOrUpdate(qi);
        //restricao
        QuestaoRestricao qr = new QuestaoRestricao();

        qr.setRestricao(restricao.getBytes());
        qr.setIdQuestao(String.valueOf(q.getId()));
        qr.setQuestao(q);

        GenericDAO<QuestaoRestricao> gqr = new GenericDAO<>();
        gqr.saveOrUpdate(qr);
        //entradaexemplo
        QuestaoEntrada qe = new QuestaoEntrada();

        String[] proSplit = entradaExemplo.split("\n");
        entradaFormat = "";
        for (String str : proSplit) {
            entradaFormat += str.trim() + "\\n";
        }

        qe.setEntrada(entradaExemplo.getBytes());

        qe.setIdQuestao(String.valueOf(q.getId()));
        qe.setQuestao(q);

        GenericDAO<QuestaoEntrada> gqe = new GenericDAO<>();
        gqe.saveOrUpdate(qe);

        //
        JDoodle j = new JDoodle();

        JDoodleOutputFormat output = j.post(request, response, uploadedFile, compilerId, entradaFormat);

        QuestaoSaidaEsperada qs = new QuestaoSaidaEsperada();

        System.out.println("convertendo:");
        String[] proSplit2 = output.getCodeOutput().split("\\\\n");
        saidaFormat = "";
        for (String str2 : proSplit2) {
            saidaFormat += str2 + "\n";
        }
        System.out.println(saidaFormat);

        qs.setSaidaEsperada(saidaFormat.getBytes());
        qs.setIdQuestao(String.valueOf(q.getId()));
        qs.setQuestao(q);

        GenericDAO<QuestaoSaidaEsperada> gqs = new GenericDAO<>();
        gqs.saveOrUpdate(qs);

        q.setCodigoFonteGabarito(Files.readAllBytes(Paths.get(uploadedFile.getPath())));
        q.setTempoExec(Double.parseDouble(output.getCpuTime()));
        gq.saveOrUpdate(q);

        response.sendRedirect("../djudge/questao/success.jsp?id=" + q.getId());

    }

}
