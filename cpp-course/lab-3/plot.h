#ifndef PLOT_H
#define PLOT_H

#include <QWidget>
#include <QtDataVisualization>

class Plot
{
public:
    QWidget *m_container;
    Q3DSurface *m_graph;

    Plot();
    ~Plot();

    void createSinc1();
    void createSinc2();

public slots:
    void setPlot(int func_number);

private:
    QSurface3DSeries *m_series_sinc1;
    QSurfaceDataProxy *m_data_proxy_sinc1;
    QSurfaceDataArray *m_data_array_sinc1;

    QSurface3DSeries *m_series_sinc2;
    QSurfaceDataProxy *m_data_proxy_sinc2;
    QSurfaceDataArray *m_data_array_sinc2;
};

#endif // PLOT_H
