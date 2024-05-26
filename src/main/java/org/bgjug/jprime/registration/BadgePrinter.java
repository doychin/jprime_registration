package org.bgjug.jprime.registration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.bgjug.jprime.registration.model.VisitorData;

public class BadgePrinter {

    private BadgePrinter() {
    }

    public static JasperPrint printBadge(String event, VisitorData visitorData, boolean printDay1,
        boolean printDay2, boolean gift, boolean print) {
        try {
            String resourceName = "/badge.jasper";
            InputStream reportTemplate = BadgePrinter.class.getResourceAsStream(resourceName);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", visitorData.getName());
            parameters.put("company", visitorData.getCompany());
            parameters.put("type", visitorData.getType());
            parameters.put("email", "not@this.year");//visitorData.getEmail());
            parameters.put("event", event);

            parameters.put("gift", gift);
            parameters.put("lunch", "Lunch day One");

            JasperPrintManager printManager =
                JasperPrintManager.getInstance(DefaultJasperReportsContext.getInstance());

            JasperPrint jasperPrint = null;
            if (printDay1) {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                if (print) {
                    printManager.print(jasperPrint, false);
                }
                parameters.put("gift", false);
            }

            if (printDay2) {
                parameters.put("lunch", "Lunch day Two");
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                if (print) {
                    printManager.print(jasperPrint, false);
                }
            }
            return jasperPrint;
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }
}
