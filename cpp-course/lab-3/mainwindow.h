#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include "plot.h"

#include <QAction>
#include <QCheckBox>
#include <QGroupBox>
#include <QHBoxLayout>
#include <QMainWindow>
#include <QMenu>
#include <QMenuBar>
#include <QPushButton>
#include <QRadioButton>
#include <QSettings>
#include <QSlider>
#include <QStatusBar>
#include <QString>
#include <QStyle>
#include <QToolBar>
#include <QVBoxLayout>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = nullptr);

    void setButtons(Plot *graphics);

private:
    QMenu *m_menu;
    QVBoxLayout *m_vertical;
    QHBoxLayout *m_horizontal;

    QGroupBox *m_language;
    QGroupBox *m_plot;
    QGroupBox *m_selection_mode;
    QGroupBox *m_colomn_range;
    QGroupBox *m_row_range;
    QGroupBox *m_custom_gradient;

    QSettings m_settings;
    QAction *m_save_action;
    QAction *m_upload_action;

    QRadioButton *m_english;
    QRadioButton *m_russian;

    QCheckBox *m_show_grid;
    QCheckBox *m_show_label;
    QCheckBox *m_show_label_border;

    QRadioButton *m_sinc1;
    QRadioButton *m_sinc2;

    QRadioButton *m_item;
    QRadioButton *m_no_selection;

    QSlider *m_column_range_min;
    QSlider *m_column_range_max;
    QSlider *m_row_range_min;
    QSlider *m_row_range_max;

    QLinearGradient m_gradient_color1;
    QLinearGradient m_gradient_color2;

    QPushButton *m_gradient1;
    QPushButton *m_gradient2;

    void setAxis(Plot *graphics);
    void setTheme(Plot *graphics);
    void setColor();

private slots:
    void turnGrid(Q3DSurface *graph);
    void turnLabel(Q3DSurface *graph);
    void turnLabelBorder(Q3DSurface *graph);
    void setGradient(int gradient_number, Q3DSurface *graph);
    void saveSettings();
    void uploadSettings();
    void setLanguage(int lang);
};

#endif // MAINWINDOW_H
