package hdm.csm.smarthome.phone.openhab;

public class StatusInfo {
    private String status;
    private String statusDetail;

    public StatusInfo(String status, String statusDetail) {
        this.status = status;
        this.statusDetail = statusDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusInfo{");
        sb.append("status='").append(status).append('\'');
        sb.append(", statusDetail='").append(statusDetail).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
