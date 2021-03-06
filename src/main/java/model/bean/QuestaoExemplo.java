package model.bean;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import model.dao.BaseEntity;

/**
 *
 * @author eddunic
 */
@Entity
public class QuestaoExemplo implements Serializable, BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, columnDefinition = "longblob")
    private byte[] exemplo;

    @ManyToOne(cascade={ CascadeType.REMOVE })
    private Questao questao;

    public QuestaoExemplo() {
    }

    public QuestaoExemplo(Long id, byte[] exemplo) {
        this.id = id;
        this.exemplo = exemplo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getExemplo() {
        return exemplo;
    }

    public void setExemplo(byte[] exemplo) {
        this.exemplo = exemplo;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }

}
