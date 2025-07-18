package Controller.reservas;

import DAO.*;
import Model.*;
import Utils.Conexion;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "ReservaDetalleServlet", value = "/reserva_detalle")
public class ReservaDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idReservaStr = request.getParameter("id");
        String generarPdf = request.getParameter("pdf");

        if (idReservaStr == null || idReservaStr.trim().isEmpty()) {
            response.sendRedirect("reservas");
            return;
        }

        if ("true".equals(generarPdf)) {
            generarPDFReserva(request, response);
        } else {
            mostrarDetalleReserva(request, response);
        }
    }

    /**
     * Mostrar detalle completo de la reserva
     */
    private void mostrarDetalleReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idReservaStr = request.getParameter("id");

        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();

            // 1. Obtener reserva principal
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);

            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }

            // 2. Obtener cliente
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            ClienteDTO cliente = clienteDAO.buscarPorId(reserva.getIdCliente());

            // 3. Obtener empleado responsable
            String nombreEmpleado = obtenerNombreEmpleado(connection, reserva.getIdEmpleado());

            // 4. Obtener habitaciones asignadas
            List<HabitacionDetalleDTO> habitaciones = obtenerHabitacionesReserva(connection, idReserva);

            // 5. Obtener consumos de servicios
            ConsumoServicioDAO consumoServicioDAO = new ConsumoServicioDAO(connection);
            List<ConsumoServicioDTO> serviciosConsumidos = consumoServicioDAO.listarPorReserva(idReserva);
            BigDecimal totalServicios = consumoServicioDAO.obtenerTotalConsumosPorReserva(idReserva);

            // 6. Obtener consumos de productos
            ConsumoProductoDAO consumoProductoDAO = new ConsumoProductoDAO(connection);
            List<ConsumoProductoDTO> productosConsumidos = consumoProductoDAO.listarPorReserva(idReserva);
            BigDecimal totalProductos = consumoProductoDAO.obtenerTotalConsumosPorReserva(idReserva);

            // 7. Obtener pagos realizados
            List<PagoDTO> pagos = obtenerPagosReserva(connection, idReserva);
            BigDecimal totalPagado = calcularTotalPagado(pagos);

            // 8. Obtener comprobantes
            ComprobanteDAO comprobanteDAO = new ComprobanteDAO(connection);
            List<ComprobanteDTO> comprobantes = comprobanteDAO.listarPorReserva(idReserva);

            // 9. Calcular totales
            BigDecimal subtotalConsumos = totalServicios.add(totalProductos);
            BigDecimal impuestosConsumos = subtotalConsumos.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalConsumos = subtotalConsumos.add(impuestosConsumos);

            BigDecimal subtotalGeneral = reserva.getSubtotal().add(subtotalConsumos);
            BigDecimal impuestosGenerales = reserva.getImpuestos().add(impuestosConsumos);
            BigDecimal totalGeneral = subtotalGeneral.add(impuestosGenerales);

            BigDecimal saldoPendiente = totalGeneral.subtract(totalPagado);

            // 10. Establecer atributos para el JSP
            request.setAttribute("reserva", reserva);
            request.setAttribute("cliente", cliente);
            request.setAttribute("nombreEmpleado", nombreEmpleado);
            request.setAttribute("habitaciones", habitaciones);
            request.setAttribute("serviciosConsumidos", serviciosConsumidos);
            request.setAttribute("productosConsumidos", productosConsumidos);
            request.setAttribute("pagos", pagos);
            request.setAttribute("comprobantes", comprobantes);
            request.setAttribute("totalServicios", totalServicios);
            request.setAttribute("totalProductos", totalProductos);
            request.setAttribute("subtotalConsumos", subtotalConsumos);
            request.setAttribute("subtotalGeneral", subtotalGeneral);
            request.setAttribute("impuestosConsumos", impuestosConsumos);
            request.setAttribute("impuestosGenerales", impuestosGenerales);
            request.setAttribute("totalGeneral", totalGeneral);
            request.setAttribute("totalPagado", totalPagado);
            request.setAttribute("saldoPendiente", saldoPendiente);

            request.getRequestDispatcher("Reservas/reserva_detalle.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de reserva inválido");
            response.sendRedirect("reservas");
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo detalle de reserva: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al obtener detalle: " + e.getMessage());
            response.sendRedirect("reservas");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    /* ignore */ }
            }
        }
    }

    /**
     * Generar PDF de la reserva
     */
    // REEMPLAZA el método generarPDFReserva en tu ReservaDetalleServlet
    /**
     * Generar PDF de la reserva - VERSIÓN CORREGIDA
     */
    private void generarPDFReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idReservaStr = request.getParameter("id");

        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();

            // Obtener todos los datos de la reserva
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);

            if (reserva == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reserva no encontrada");
                return;
            }

            ClienteDAO clienteDAO = new ClienteDAO(connection);
            ClienteDTO cliente = clienteDAO.buscarPorId(reserva.getIdCliente());

            String nombreEmpleado = obtenerNombreEmpleado(connection, reserva.getIdEmpleado());
            List<HabitacionDetalleDTO> habitaciones = obtenerHabitacionesReserva(connection, idReserva);

            ConsumoServicioDAO servicioDAO = new ConsumoServicioDAO(connection);
            List<ConsumoServicioDTO> servicios = servicioDAO.listarPorReserva(idReserva);
            BigDecimal totalServicios = servicioDAO.obtenerTotalConsumosPorReserva(idReserva);

            ConsumoProductoDAO productoDAO = new ConsumoProductoDAO(connection);
            List<ConsumoProductoDTO> productos = productoDAO.listarPorReserva(idReserva);
            BigDecimal totalProductos = productoDAO.obtenerTotalConsumosPorReserva(idReserva);

            List<PagoDTO> pagos = obtenerPagosReserva(connection, idReserva);
            BigDecimal totalPagado = calcularTotalPagado(pagos);

            // ===== CÁLCULOS CORREGIDOS =====
            // 1. Subtotal de consumos (sin IGV)
            BigDecimal subtotalConsumos = totalServicios.add(totalProductos);

            // 2. IGV de consumos (18%)
            BigDecimal igvConsumos = subtotalConsumos.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);

            // 3. Total de consumos (con IGV)
            BigDecimal totalConsumos = subtotalConsumos.add(igvConsumos);

            // 4. Subtotal general (hospedaje + consumos sin IGV)
            BigDecimal subtotalGeneral = reserva.getSubtotal().add(subtotalConsumos);

            // 5. IGV total (IGV hospedaje + IGV consumos)
            BigDecimal igvTotal = reserva.getImpuestos().add(igvConsumos);

            // 6. Total general CORREGIDO
            BigDecimal totalGeneral = subtotalGeneral.add(igvTotal);

            // 7. Saldo pendiente
            BigDecimal saldoPendiente = totalGeneral.subtract(totalPagado);

            // Generar PDF MEJORADO
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // === CONFIGURAR FUENTES Y COLORES ===
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont smallFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Colores personalizados
            DeviceRgb azulPrincipal = new DeviceRgb(33, 150, 243);
            DeviceRgb grisClaro = new DeviceRgb(245, 245, 245);
            DeviceRgb verdeDinero = new DeviceRgb(76, 175, 80);
            DeviceRgb rojoAdvertencia = new DeviceRgb(244, 67, 54);

            // === ENCABEZADO PROFESIONAL ===
            Table headerTable = new Table(new float[]{1, 2});
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Logo/Nombre del hotel (lado izquierdo)
            Cell logoCell = new Cell()
                    .add(new Paragraph("🏨").setFont(titleFont).setFontSize(40).setFontColor(azulPrincipal))
                    .add(new Paragraph("HOTEL RIZZO").setFont(titleFont).setFontSize(20).setFontColor(azulPrincipal))
                    .add(new Paragraph("Sistema de Gestión Hotelera").setFont(normalFont).setFontSize(10))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(10);

            // Información de la reserva (lado derecho)
            Cell infoCell = new Cell()
                    .add(new Paragraph("COMPROBANTE DE RESERVA").setFont(boldFont).setFontSize(16).setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("N° " + reserva.getNumeroReserva()).setFont(titleFont).setFontSize(18).setTextAlignment(TextAlignment.RIGHT).setFontColor(azulPrincipal))
                    .add(new Paragraph("Fecha: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFont(normalFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPadding(10);

            headerTable.addCell(logoCell);
            headerTable.addCell(infoCell);
            document.add(headerTable);

            // Línea separadora
            document.add(new Paragraph("\n"));
            LineSeparator lineSeparator = new LineSeparator(new SolidLine(2));
            lineSeparator.setStrokeColor(azulPrincipal);
            document.add(lineSeparator);
            document.add(new Paragraph("\n"));

            // === INFORMACIÓN DEL CLIENTE (DESTACADA) ===
            Table clienteTable = new Table(1);
            clienteTable.setWidth(UnitValue.createPercentValue(100));

            Cell clienteHeader = new Cell()
                    .add(new Paragraph("INFORMACIÓN DEL CLIENTE").setFont(boldFont).setFontSize(14).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(azulPrincipal)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(10)
                    .setBorder(Border.NO_BORDER);

            clienteTable.addCell(clienteHeader);

            if (cliente != null) {
                String clienteInfo = String.format(
                        "Nombre: %s %s\nDNI: %s\nTeléfono: %s\nCorreo: %s",
                        cliente.getNombre(),
                        cliente.getApellido(),
                        cliente.getDni(),
                        cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado",
                        cliente.getCorreo() != null ? cliente.getCorreo() : "No registrado"
                );

                Cell clienteInfo_cell = new Cell()
                        .add(new Paragraph(clienteInfo).setFont(normalFont).setFontSize(11))
                        .setBackgroundColor(grisClaro)
                        .setPadding(15)
                        .setBorder(Border.NO_BORDER);

                clienteTable.addCell(clienteInfo_cell);
            }

            document.add(clienteTable);
            document.add(new Paragraph("\n"));

            // === DETALLES DE LA RESERVA ===
            Table reservaTable = new Table(new float[]{1, 1, 1, 1});
            reservaTable.setWidth(UnitValue.createPercentValue(100));

            // Headers
            String[] headers = {"Fecha Entrada", "Fecha Salida", "Huéspedes", "Estado"};
            for (String header : headers) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(header).setFont(boldFont).setFontSize(11).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(azulPrincipal)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(8);
                reservaTable.addCell(headerCell);
            }

            // Datos
            reservaTable.addCell(new Cell().add(new Paragraph(reserva.getFechaEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)).setPadding(8));
            reservaTable.addCell(new Cell().add(new Paragraph(reserva.getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)).setPadding(8));
            reservaTable.addCell(new Cell().add(new Paragraph(String.valueOf(reserva.getNumHuespedes())).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)).setPadding(8));

            // Estado con color
            Cell estadoCell = new Cell()
                    .add(new Paragraph(reserva.getEstado()).setFont(boldFont).setTextAlignment(TextAlignment.CENTER));

            switch (reserva.getEstado()) {
                case "Confirmada":
                    estadoCell.setBackgroundColor(new DeviceRgb(200, 255, 200));
                    break;
                case "CheckIn":
                    estadoCell.setBackgroundColor(new DeviceRgb(200, 200, 255));
                    break;
                case "CheckOut":
                    estadoCell.setBackgroundColor(new DeviceRgb(220, 220, 220));
                    break;
                case "Cancelada":
                    estadoCell.setBackgroundColor(new DeviceRgb(255, 200, 200));
                    break;
                default:
                    estadoCell.setBackgroundColor(new DeviceRgb(255, 255, 200));
            }
            estadoCell.setPadding(8);
            reservaTable.addCell(estadoCell);

            document.add(reservaTable);
            document.add(new Paragraph("\n"));

            // === HABITACIONES (MEJORADO) ===
            document.add(new Paragraph("HABITACIONES ASIGNADAS").setFont(boldFont).setFontSize(14).setFontColor(azulPrincipal));

            Table habitacionesTable = new Table(new float[]{2, 2, 1, 1});
            habitacionesTable.setWidth(UnitValue.createPercentValue(100));

            // Headers habitaciones
            String[] habHeaders = {"Habitación", "Tipo", "Capacidad", "Precio/Noche"};
            for (String header : habHeaders) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(header).setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(new DeviceRgb(100, 100, 100))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6);
                habitacionesTable.addCell(headerCell);
            }

            // Datos habitaciones
            for (HabitacionDetalleDTO hab : habitaciones) {
                habitacionesTable.addCell(new Cell().add(new Paragraph("Hab. " + hab.getNumeroHabitacion()).setFont(boldFont).setFontSize(12)).setPadding(8).setTextAlignment(TextAlignment.CENTER));
                habitacionesTable.addCell(new Cell().add(new Paragraph(hab.getTipoHabitacion()).setFont(normalFont)).setPadding(8));
                habitacionesTable.addCell(new Cell().add(new Paragraph(hab.getCapacidadPersonas() + " pax").setFont(normalFont)).setPadding(8).setTextAlignment(TextAlignment.CENTER));
                habitacionesTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", hab.getPrecioNoche())).setFont(normalFont).setFontColor(verdeDinero)).setPadding(8).setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(habitacionesTable);
            document.add(new Paragraph("\n"));

            // === CONSUMOS (SI HAY) ===
            if (!servicios.isEmpty() || !productos.isEmpty()) {
                document.add(new Paragraph("SERVICIOS Y PRODUCTOS CONSUMIDOS").setFont(boldFont).setFontSize(14).setFontColor(azulPrincipal));

                Table consumosTable = new Table(new float[]{3, 1, 1, 1});
                consumosTable.setWidth(UnitValue.createPercentValue(100));

                // Headers consumos
                String[] consumoHeaders = {"Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
                for (String header : consumoHeaders) {
                    Cell headerCell = new Cell()
                            .add(new Paragraph(header).setFont(boldFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(new DeviceRgb(150, 100, 50))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(6);
                    consumosTable.addCell(headerCell);
                }

                // Servicios
                for (ConsumoServicioDTO servicio : servicios) {
                    consumosTable.addCell(new Cell().add(new Paragraph("🔧 " + servicio.getNombreServicio()).setFont(normalFont)).setPadding(6));
                    consumosTable.addCell(new Cell().add(new Paragraph(String.valueOf(servicio.getCantidad())).setFont(normalFont)).setPadding(6).setTextAlignment(TextAlignment.CENTER));
                    consumosTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", servicio.getPrecioUnitario())).setFont(normalFont)).setPadding(6).setTextAlignment(TextAlignment.RIGHT));
                    consumosTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", servicio.getSubtotal())).setFont(normalFont).setFontColor(verdeDinero)).setPadding(6).setTextAlignment(TextAlignment.RIGHT));
                }

                // Productos
                for (ConsumoProductoDTO producto : productos) {
                    consumosTable.addCell(new Cell().add(new Paragraph("🛒 " + producto.getNombreProducto()).setFont(normalFont)).setPadding(6));
                    consumosTable.addCell(new Cell().add(new Paragraph(String.valueOf(producto.getCantidad())).setFont(normalFont)).setPadding(6).setTextAlignment(TextAlignment.CENTER));
                    consumosTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", producto.getPrecioUnitario())).setFont(normalFont)).setPadding(6).setTextAlignment(TextAlignment.RIGHT));
                    consumosTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", producto.getPrecioUnitario().multiply(new BigDecimal(producto.getCantidad())))).setFont(normalFont).setFontColor(verdeDinero)).setPadding(6).setTextAlignment(TextAlignment.RIGHT));
                }

                document.add(consumosTable);
                document.add(new Paragraph("\n"));
            }

            // === RESUMEN FINANCIERO CORREGIDO ===
            Table resumenTable = new Table(new float[]{3, 1});
            resumenTable.setWidth(UnitValue.createPercentValue(60));
            resumenTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);

            // Subtotal hospedaje
            resumenTable.addCell(new Cell().add(new Paragraph("Subtotal Hospedaje:").setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER));
            resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", reserva.getSubtotal())).setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            // Subtotal consumos (si hay)
            if (subtotalConsumos.compareTo(BigDecimal.ZERO) > 0) {
                resumenTable.addCell(new Cell().add(new Paragraph("Subtotal Consumos:").setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER));
                resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", subtotalConsumos)).setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            }

            // Subtotal general
            resumenTable.addCell(new Cell().add(new Paragraph("Subtotal General:").setFont(boldFont)).setPadding(8).setBorder(Border.NO_BORDER));
            resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", subtotalGeneral)).setFont(boldFont)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            // IGV total
            resumenTable.addCell(new Cell().add(new Paragraph("IGV (18%):").setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER));
            resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", igvTotal)).setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            // Línea separadora para total
            Cell separatorCell1 = new Cell().setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1));
            Cell separatorCell2 = new Cell().setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1));
            resumenTable.addCell(separatorCell1);
            resumenTable.addCell(separatorCell2);

            // TOTAL GENERAL CORREGIDO
            resumenTable.addCell(new Cell().add(new Paragraph("TOTAL GENERAL:").setFont(titleFont).setFontSize(14)).setPadding(8).setBorder(Border.NO_BORDER).setBackgroundColor(azulPrincipal).setFontColor(ColorConstants.WHITE));
            resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", totalGeneral)).setFont(titleFont).setFontSize(14)).setPadding(8).setBorder(Border.NO_BORDER).setBackgroundColor(azulPrincipal).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.RIGHT));

            // Pagado y saldo
            resumenTable.addCell(new Cell().add(new Paragraph("Total Pagado:").setFont(normalFont)).setPadding(8).setBorder(Border.NO_BORDER));
            resumenTable.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", totalPagado)).setFont(normalFont).setFontColor(verdeDinero)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            // Saldo con color
            Cell saldoLabelCell = new Cell().add(new Paragraph("Saldo Pendiente:").setFont(boldFont)).setPadding(8).setBorder(Border.NO_BORDER);
            Cell saldoValueCell = new Cell().add(new Paragraph("S/. " + String.format("%.2f", saldoPendiente)).setFont(boldFont)).setPadding(8).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);

            if (saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
                saldoValueCell.setFontColor(rojoAdvertencia);
            } else {
                saldoValueCell.setFontColor(verdeDinero);
            }

            resumenTable.addCell(saldoLabelCell);
            resumenTable.addCell(saldoValueCell);

            document.add(resumenTable);

            // === PIE DE PÁGINA PROFESIONAL ===
            document.add(new Paragraph("\n\n\n"));

            Table footerTable = new Table(1);
            footerTable.setWidth(UnitValue.createPercentValue(100));

            String footerText = String.format(
                    "Documento generado el %s\n"
                    + "Empleado responsable: %s\n"
                    + "Hotel Rizzo - Sistema de Gestión Hotelera\n"
                    + "¡Gracias por elegir nuestros servicios!",
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    nombreEmpleado
            );

            Cell footerCell = new Cell()
                    .add(new Paragraph(footerText).setFont(smallFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(grisClaro)
                    .setPadding(15)
                    .setBorder(Border.NO_BORDER);

            footerTable.addCell(footerCell);
            document.add(footerTable);

            document.close();

            // Configurar respuesta HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Reserva_" + reserva.getNumeroReserva() + ".pdf\"");
            response.setContentLength(baos.size());

            // Escribir PDF al response
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();

            System.out.println("✅ PDF profesional generado para reserva: " + reserva.getNumeroReserva());

        } catch (Exception e) {
            System.err.println("❌ Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando PDF");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    /* ignore */ }
            }
        }
    }

    // ===============================================
    // MÉTODOS AUXILIARES
    // ===============================================
    /**
     * Obtener nombre del empleado
     */
    private String obtenerNombreEmpleado(Connection connection, int idEmpleado) throws SQLException {
        String sql = """
            SELECT p.nombre + ' ' + p.apellido AS nombre_completo
            FROM Empleados e
            JOIN Personas p ON e.id_empleado = p.id_persona
            WHERE e.id_empleado = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        }
        return "Empleado no encontrado";
    }

    /**
     * Obtener habitaciones de la reserva
     */
    private List<HabitacionDetalleDTO> obtenerHabitacionesReserva(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT h.id_habitacion, h.numero, th.nombre as tipo_nombre, rh.precio_noche,
                   th.capacidad_personas, th.descripcion, h.estado, rh.id_detalle,
                   DATEDIFF(day, rh.fecha_entrada, rh.fecha_salida) as total_noches,
                   (DATEDIFF(day, rh.fecha_entrada, rh.fecha_salida) * rh.precio_noche) as subtotal,
                   rh.observaciones
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion th ON h.id_tipo = th.id_tipo
            WHERE rh.id_reserva = ?
            ORDER BY h.numero
        """;

        List<HabitacionDetalleDTO> habitaciones = new java.util.ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HabitacionDetalleDTO hab = new HabitacionDetalleDTO();
                hab.setIdDetalle(rs.getInt("id_detalle"));
                hab.setIdHabitacion(rs.getInt("id_habitacion"));
                hab.setNumeroHabitacion(rs.getString("numero"));
                hab.setTipoHabitacion(rs.getString("tipo_nombre"));
                hab.setPrecioNoche(rs.getDouble("precio_noche"));
                hab.setCapacidadPersonas(rs.getInt("capacidad_personas"));
                hab.setDescripcionTipo(rs.getString("descripcion"));
                hab.setEstadoHabitacion(rs.getString("estado"));
                hab.setTotalNoches(rs.getInt("total_noches"));
                hab.setSubtotal(rs.getBigDecimal("subtotal"));
                hab.setObservaciones(rs.getString("observaciones"));
                habitaciones.add(hab);
            }
        }
        return habitaciones;
    }

    /**
     * Obtener pagos de una reserva
     */
    private List<PagoDTO> obtenerPagosReserva(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT p.*, ISNULL(per.nombre + ' ' + per.apellido, 'Sistema') AS nombre_empleado
            FROM Pagos p
            LEFT JOIN Empleados e ON p.id_empleado = e.id_empleado
            LEFT JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.id_reserva = ? AND p.estado = 'Completado'
            ORDER BY p.fecha_pago DESC
        """;

        List<PagoDTO> pagos = new java.util.ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PagoDTO pago = new PagoDTO();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdReserva(rs.getInt("id_reserva"));
                pago.setMonto(rs.getBigDecimal("monto"));
                pago.setMetodoPago(rs.getString("metodo_pago"));
                pago.setNumeroOperacion(rs.getString("numero_operacion"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
                pago.setTipoPago(rs.getString("tipo_pago"));
                pago.setEstado(rs.getString("estado"));
                pago.setObservaciones(rs.getString("observaciones"));
                pago.setNombreEmpleado(rs.getString("nombre_empleado"));
                pagos.add(pago);
            }
        }
        return pagos;
    }

    /**
     * Calcular total pagado
     */
    private BigDecimal calcularTotalPagado(List<PagoDTO> pagos) {
        return pagos.stream()
                .map(PagoDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
