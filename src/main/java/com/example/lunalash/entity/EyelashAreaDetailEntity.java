package com.example.lunalash.entity;

import com.example.lunalash.converter.IntegerListJsonConverter;
import com.example.lunalash.converter.StringListJsonConverter;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "eyelash_area_detail")
public class EyelashAreaDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eyelash_area_detail_id")
    private Long eyelashAreaDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_item_id", nullable = false)
    private OperationItemEntity operationItem;

    @Column(name = "position")
    private String position;

    @Column(name = "lash_count")
    private Integer lashCount;

    @Convert(converter = IntegerListJsonConverter.class)
    @Column(name = "lash_lengths", columnDefinition = "json")
    private List<Integer> lashLengths;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "lash_curls", columnDefinition = "json")
    private List<String> lashCurls;

    // getter & setter
    public Long getEyelashAreaDetailId() {
    	return eyelashAreaDetailId;
    }
    public void setEyelashAreaDetailId(Long eyelashAreaDetailId) {
    	this.eyelashAreaDetailId = eyelashAreaDetailId;
    }

    public OperationItemEntity getOperationItemId() {
    	return operationItem;
    }
    public void setOperationItem(OperationItemEntity operationItemId) {
    	this.operationItem = operationItemId;
    }

    public String getPosition() {
    	return position;
    }
    public void setPosition(String position) {
    	this.position = position;
    }

    public Integer getLashCount() {
    	return lashCount;
    }
    public void setLashCount(Integer lashCount) {
    	this.lashCount = lashCount;
    }

    public List<Integer> getLashLengths() {
    	return lashLengths;
    }
    public void setLashLengths(List<Integer> lashLengths) {
    	this.lashLengths = lashLengths;
    }

    public List<String> getLashCurls() {
    	return lashCurls;
    }
    public void setLashCurls(List<String> lashCurls) {
    	this.lashCurls = lashCurls;
    }
}
