#include "plot.h"

Plot::Plot()
{
    m_graph = new Q3DSurface();
    m_container = QWidget::createWindowContainer(m_graph);

    m_series_sinc1 = new QSurface3DSeries();
    m_data_proxy_sinc1 = new QSurfaceDataProxy();

    m_series_sinc2 = new QSurface3DSeries();
    m_data_proxy_sinc2 = new QSurfaceDataProxy();
}

Plot::~Plot()
{
    if (m_data_array_sinc1) {
        m_data_array_sinc1->clear();
    }
    if (m_data_array_sinc2) {
        m_data_array_sinc2->clear();
    }
}

void Plot::createSinc1()
{
    float step_x = 20.0f / float(49);
    float step_z = 20.0f / float(49);

    m_data_array_sinc1 = new QSurfaceDataArray();
    m_data_array_sinc1->reserve(50);

    for (int i = 0; i < 50; i++) {
        QSurfaceDataRow *row = new QSurfaceDataRow(50);
        float z = qMin(10.0f, (i * step_z + -10.0f));
        int index = 0;
        for (int j = 0; j < 50; j++) {
            float x = qMin(10.0f, (j * step_x + -10.0f));
            float r = qSqrt(z * z + x * x) + 0.01f;
            float y = (qSin(r) / r + 0.24f) * 1.61f;
            (*row)[index++].setPosition(QVector3D(x, y, z));
        }
        *m_data_array_sinc1 << row;
    }
    m_data_proxy_sinc1->resetArray(m_data_array_sinc1);
    m_series_sinc1->setDataProxy(m_data_proxy_sinc1);
    m_graph->addSeries(m_series_sinc1);
}

void Plot::createSinc2()
{
    float step_x = 20.0f / float(49);
    float step_z = 20.0f / float(49);

    m_data_array_sinc2 = new QSurfaceDataArray();
    m_data_array_sinc2->reserve(50);

    for (int i = 0; i < 50; i++) {
        QSurfaceDataRow *row = new QSurfaceDataRow(50);
        float z = qMin(10.0f, (i * step_z + -10.0f));
        int index = 0;
        for (int j = 0; j < 50; j++) {
            float x = qMin(10.0f, (j * step_x + -10.0f));
            float y = (qSin(x) / x * qSin(z) / z + 0.24f) * 1.61f;
            (*row)[index++].setPosition(QVector3D(x, y, z));
        }
        *m_data_array_sinc2 << row;
    }
    m_data_proxy_sinc2->resetArray(m_data_array_sinc2);
    m_series_sinc2->setDataProxy(m_data_proxy_sinc2);
    m_graph->addSeries(m_series_sinc2);
    m_graph->seriesList().at(1)->setVisible(false);
}

void Plot::setPlot(int func_number)
{
    if (func_number == 1) {
        m_graph->seriesList().at(1)->setVisible(false);
        m_graph->seriesList().at(0)->setVisible(true);
    } else {
        m_graph->seriesList().at(0)->setVisible(false);
        m_graph->seriesList().at(1)->setVisible(true);
    }
}
