package com.chevron.edap.gomica.model;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "Jobintervalproblem")
@Table(name = "wvjobintervalproblem", schema = "gomica")
public class Jobintervalproblem {

	@Id
	@Column(name = "idrec")
	private String idrec;

	@Column(name = "idrecparent")
	private String idrecparent;

	@Column(name = "refno")
	private String refno;

	@Column(name = "status")
	private String status;

	@Column(name = "typ")
	private String typ;

	@Column(name = "typdetail")
	private String typdetail;

	@Column(name = "dttmstartdate")
	private Date dttmstartdate;

	@Column(name = "depthstart")
	private String depthstart;

	@Column(name = "accountablepty")
	private String accountablepty;

	@Column(name = "dttmenddate")
	private Date dttmenddate;

	@Column(name = "depthend")
	private String depthend;

	@Column(name = "durationnetcalc")
	private String durationnetcalc;

	@Column(name = "estcostoverride")
	private String estcostoverride;

	@Column(name = "des")
	private String des;

	@Column(name = "com")
	private String com;

	public String getIdrec() {
		return idrec;
	}

	public void setIdrec(String idrec) {
		this.idrec = idrec;
	}

	public String getIdrecparent() {
		return idrecparent;
	}

	public void setIdrecparent(String idrecparent) {
		this.idrecparent = idrecparent;
	}

	public String getRefno() {
		return refno;
	}

	public void setRefno(String refno) {
		this.refno = refno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getTypdetail() {
		return typdetail;
	}

	public void setTypdetail(String typdetail) {
		this.typdetail = typdetail;
	}

	public Date getDttmstartdate() {
		return dttmstartdate;
	}

	public void setDttmstartdate(Date dttmstartdate) {
		this.dttmstartdate = dttmstartdate;
	}

	public String getDepthstart() {
		return depthstart;
	}

	public void setDepthstart(String depthstart) {
		this.depthstart = depthstart;
	}

	public String getAccountablepty() {
		return accountablepty;
	}

	public void setAccountablepty(String accountablepty) {
		this.accountablepty = accountablepty;
	}

	public Date getDttmenddate() {
		return dttmenddate;
	}

	public void setDttmenddate(Date dttmenddate) {
		this.dttmenddate = dttmenddate;
	}

	public String getDepthend() {
		return depthend;
	}

	public void setDepthend(String depthend) {
		this.depthend = depthend;
	}

	public String getDurationnetcalc() {
		return durationnetcalc;
	}

	public void setDurationnetcalc(String durationnetcalc) {
		this.durationnetcalc = durationnetcalc;
	}

	public String getEstcostoverride() {
		return estcostoverride;
	}

	public void setEstcostoverride(String estcostoverride) {
		this.estcostoverride = estcostoverride;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public Jobintervalproblem() {
	}
}