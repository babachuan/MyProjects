package com.qhc.oa.entity;

import java.io.Serializable;

/**
 * @author 
 * 
 */
public class Permission implements Serializable {
    private Integer id;

    private String pname;

    private String uri;

    private Byte c;

    private Byte r;

    private Byte u;

    private Byte d;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Byte getC() {
        return c;
    }

    public void setC(Byte c) {
        this.c = c;
    }

    public Byte getR() {
        return r;
    }

    public void setR(Byte r) {
        this.r = r;
    }

    public Byte getU() {
        return u;
    }

    public void setU(Byte u) {
        this.u = u;
    }

    public Byte getD() {
        return d;
    }

    public void setD(Byte d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Permission other = (Permission) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPname() == null ? other.getPname() == null : this.getPname().equals(other.getPname()))
            && (this.getUri() == null ? other.getUri() == null : this.getUri().equals(other.getUri()))
            && (this.getC() == null ? other.getC() == null : this.getC().equals(other.getC()))
            && (this.getR() == null ? other.getR() == null : this.getR().equals(other.getR()))
            && (this.getU() == null ? other.getU() == null : this.getU().equals(other.getU()))
            && (this.getD() == null ? other.getD() == null : this.getD().equals(other.getD()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPname() == null) ? 0 : getPname().hashCode());
        result = prime * result + ((getUri() == null) ? 0 : getUri().hashCode());
        result = prime * result + ((getC() == null) ? 0 : getC().hashCode());
        result = prime * result + ((getR() == null) ? 0 : getR().hashCode());
        result = prime * result + ((getU() == null) ? 0 : getU().hashCode());
        result = prime * result + ((getD() == null) ? 0 : getD().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pname=").append(pname);
        sb.append(", uri=").append(uri);
        sb.append(", c=").append(c);
        sb.append(", r=").append(r);
        sb.append(", u=").append(u);
        sb.append(", d=").append(d);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}