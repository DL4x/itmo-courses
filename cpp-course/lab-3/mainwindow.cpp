#include "mainwindow.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    m_vertical = new QVBoxLayout();
    m_horizontal = new QHBoxLayout();
}

void MainWindow::setButtons(Plot *graphics)
{
    m_english = new QRadioButton();
    m_english->setText(QString("English"));
    connect(m_english, &QRadioButton::toggled, this, [=]() { MainWindow::setLanguage(1); });

    m_russian = new QRadioButton();
    m_russian->setText(QString("Русский"));
    connect(m_russian, &QRadioButton::toggled, this, [=]() { MainWindow::setLanguage(2); });

    m_language = new QGroupBox();
    QVBoxLayout *language = new QVBoxLayout();
    language->addWidget(m_english);
    language->addWidget(m_russian);
    m_language->setLayout(language);
    m_vertical->addWidget(m_language);

    m_show_grid = new QCheckBox();
    connect(m_show_grid, &QCheckBox::stateChanged, this, [=]() {
        MainWindow::turnGrid(graphics->m_graph);
    });
    m_vertical->addWidget(m_show_grid);

    m_show_label = new QCheckBox();
    connect(m_show_label, &QCheckBox::stateChanged, this, [=]() {
        MainWindow::turnLabel(graphics->m_graph);
    });
    m_vertical->addWidget(m_show_label);

    m_show_label_border = new QCheckBox();
    m_show_label_border->setEnabled(false);
    connect(m_show_label_border, &QCheckBox::stateChanged, this, [=]() {
        MainWindow::turnLabelBorder(graphics->m_graph);
    });
    m_vertical->addWidget(m_show_label_border);

    this->setAxis(graphics);
    this->setTheme(graphics);

    m_sinc1 = new QRadioButton();
    m_sinc1->setText(QStringLiteral("Sinc1"));
    m_sinc1->toggle();
    connect(m_sinc1, &QRadioButton::toggled, this, [=]() { graphics->setPlot(1); });

    m_sinc2 = new QRadioButton();
    m_sinc2->setText(QStringLiteral("Sinc2"));
    connect(m_sinc2, &QRadioButton::toggled, this, [=]() { graphics->setPlot(2); });

    m_plot = new QGroupBox();
    QVBoxLayout *plots = new QVBoxLayout();
    plots->addWidget(m_sinc1);
    plots->addWidget(m_sinc2);
    m_plot->setLayout(plots);
    m_vertical->addWidget(m_plot);

    m_no_selection = new QRadioButton();
    connect(m_no_selection, &QRadioButton::toggled, this, [=]() {
        graphics->m_graph->setSelectionMode(QAbstract3DGraph::SelectionNone);
    });
    m_no_selection->toggle();

    m_item = new QRadioButton();
    m_item->setEnabled(false);
    connect(m_item, &QRadioButton::toggled, this, [=]() {
        graphics->m_graph->setSelectionMode(QAbstract3DGraph::SelectionItem);
    });

    m_selection_mode = new QGroupBox();
    QVBoxLayout *mode = new QVBoxLayout();
    mode->addWidget(m_no_selection);
    mode->addWidget(m_item);
    m_selection_mode->setLayout(mode);
    m_vertical->addWidget(m_selection_mode);

    m_column_range_min = new QSlider(Qt::Horizontal);
    m_column_range_min->setMinimum(0);
    m_column_range_min->setMaximum(48);
    m_column_range_min->setValue(0);
    connect(m_column_range_min, &QSlider::valueChanged, this, [=]() {
        graphics->m_graph->axisX()->setMin(-10 + m_column_range_min->value() * 0.4f);
    });

    m_column_range_max = new QSlider(Qt::Horizontal);
    m_column_range_max->setMinimum(2);
    m_column_range_max->setMaximum(50);
    m_column_range_max->setValue(50);
    connect(m_column_range_max, &QSlider::valueChanged, this, [=]() {
        graphics->m_graph->axisX()->setMax(-10 + m_column_range_max->value() * 0.4f);
    });

    m_colomn_range = new QGroupBox();
    QVBoxLayout *colomn_slider = new QVBoxLayout();
    colomn_slider->addWidget(m_column_range_min);
    colomn_slider->addWidget(m_column_range_max);
    m_colomn_range->setLayout(colomn_slider);
    m_vertical->addWidget(m_colomn_range);

    m_row_range_min = new QSlider(Qt::Horizontal);
    m_row_range_min->setMinimum(0);
    m_row_range_min->setMaximum(48);
    m_row_range_min->setValue(0);
    connect(m_row_range_min, &QSlider::valueChanged, this, [=]() {
        graphics->m_graph->axisZ()->setMin(-10 + m_row_range_min->value() * 0.4f);
    });

    m_row_range_max = new QSlider(Qt::Horizontal);
    m_row_range_max->setMinimum(2);
    m_row_range_max->setMaximum(50);
    m_row_range_max->setValue(50);
    connect(m_row_range_max, &QSlider::valueChanged, this, [=]() {
        graphics->m_graph->axisZ()->setMax(-10 + m_row_range_max->value() * 0.4f);
    });

    m_row_range = new QGroupBox();
    QVBoxLayout *row_slider = new QVBoxLayout();
    row_slider->addWidget(m_row_range_min);
    row_slider->addWidget(m_row_range_max);
    m_row_range->setLayout(row_slider);
    m_vertical->addWidget(m_row_range);

    this->setColor();

    m_gradient1 = new QPushButton();
    m_gradient1->setFixedSize(55, 150);
    m_gradient1->setStyleSheet(
        QString("background: qlineargradient(x1:0, y1:0, x2:0, y2:1, stop:1.0 #0d0887, stop:0.67 "
                "#7e03a8, stop:0.43 #cc4778, stop:0.25 #f89540, stop:0.1 #f0f921);"));
    connect(m_gradient1, &QPushButton::clicked, this, [=]() {
        MainWindow::setGradient(1, graphics->m_graph);
    });

    m_gradient2 = new QPushButton();
    m_gradient2->setFixedSize(55, 150);
    m_gradient2->setStyleSheet(
        QString("background: qlineargradient(x1:0, y1:0, x2:0, y2:1, stop:1.0 #440154, stop:0.75 "
                "#3b528b, stop:0.54 #21918c, stop:0.33 #5ec962, stop:0.15 #fde725);"));
    connect(m_gradient2, &QPushButton::clicked, this, [=]() {
        MainWindow::setGradient(2, graphics->m_graph);
    });

    m_custom_gradient = new QGroupBox();
    QHBoxLayout *gradient = new QHBoxLayout();
    gradient->addWidget(m_gradient1);
    gradient->addWidget(m_gradient2);
    m_custom_gradient->setLayout(gradient);
    m_vertical->addWidget(m_custom_gradient);

    m_horizontal->addWidget(graphics->m_container, 1);
    m_horizontal->addLayout(m_vertical);

    m_save_action = new QAction(style()->standardIcon(QStyle::SP_DialogSaveButton),
                                QString("&Save"),
                                this);
    m_save_action->setShortcuts(QKeySequence::Save);
    m_save_action->setStatusTip(QString("Save settings"));
    connect(m_save_action, &QAction::triggered, this, &MainWindow::saveSettings);

    m_upload_action = new QAction(style()->standardIcon(QStyle::SP_FileDialogContentsView),
                                  QString("&Upload"),
                                  this);
    m_upload_action->setShortcuts(QKeySequence::Refresh);
    m_upload_action->setStatusTip(QString("Upload settings"));
    connect(m_upload_action, &QAction::triggered, this, &MainWindow::uploadSettings);

    QMenuBar *menu_bar = new QMenuBar();
    m_menu = menu_bar->addMenu(QString("&Settings"));
    m_menu->addAction(m_save_action);
    m_menu->addAction(m_upload_action);
    this->setMenuBar(menu_bar);

    QToolBar *tool_bar = addToolBar(QString("Toolbar"));
    tool_bar->addAction(m_save_action);
    tool_bar->addAction(m_upload_action);
    this->addToolBar(tool_bar);

    QWidget *widget = new QWidget();
    widget->setLayout(m_horizontal);

    this->setCentralWidget(widget);

    m_english->toggle();
}

void MainWindow::setAxis(Plot *graphics)
{
    graphics->m_graph->axisX()->setLabelFormat("");
    graphics->m_graph->axisX()->setTitle(QStringLiteral("X"));
    graphics->m_graph->axisX()->setTitleVisible(false);
    graphics->m_graph->axisY()->setLabelFormat("");
    graphics->m_graph->axisY()->setTitle(QStringLiteral("Y"));
    graphics->m_graph->axisY()->setTitleVisible(false);
    graphics->m_graph->axisZ()->setLabelFormat("");
    graphics->m_graph->axisZ()->setTitle(QStringLiteral("Z"));
    graphics->m_graph->axisZ()->setTitleVisible(false);
}

void MainWindow::setTheme(Plot *graphics)
{
    graphics->m_graph->activeTheme()->setGridEnabled(false);
    graphics->m_graph->activeTheme()->setLabelBackgroundEnabled(false);
    graphics->m_graph->activeTheme()->setGridLineColor(Qt::gray);
}

void MainWindow::setColor()
{
    m_gradient_color1.setColorAt(0.0, QString("#0d0887"));
    m_gradient_color1.setColorAt(0.33, QString("#7e03a8"));
    m_gradient_color1.setColorAt(0.57, QString("#cc4778"));
    m_gradient_color1.setColorAt(0.75, QString("#f89540"));
    m_gradient_color1.setColorAt(0.9, QString("#f0f921"));

    m_gradient_color2.setColorAt(0.0, QString("#440154"));
    m_gradient_color2.setColorAt(0.25, QString("#3b528b"));
    m_gradient_color2.setColorAt(0.46, QString("#21918c"));
    m_gradient_color2.setColorAt(0.67, QString("#5ec962"));
    m_gradient_color2.setColorAt(0.85, QString("#fde725"));
}

void MainWindow::turnGrid(Q3DSurface *graph)
{
    if (m_show_grid->isChecked()) {
        graph->activeTheme()->setGridEnabled(true);
    } else {
        graph->activeTheme()->setGridEnabled(false);
    }
}

void MainWindow::turnLabel(Q3DSurface *graph)
{
    if (m_show_label->isChecked()) {
        graph->axisX()->setLabelFormat(QString("%.1f"));
        graph->axisX()->setTitleVisible(true);
        graph->axisY()->setLabelFormat(QString("%.1f"));
        graph->axisY()->setTitleVisible(true);
        graph->axisZ()->setLabelFormat(QString("%.1f"));
        graph->axisZ()->setTitleVisible(true);
        m_show_label_border->setEnabled(true);
        m_item->setEnabled(true);
    } else {
        graph->axisX()->setLabelFormat(QString(""));
        graph->axisX()->setTitleVisible(false);
        graph->axisY()->setLabelFormat(QString(""));
        graph->axisY()->setTitleVisible(false);
        graph->axisZ()->setLabelFormat(QString(""));
        graph->axisZ()->setTitleVisible(false);
        m_show_label_border->setEnabled(false);
        m_item->setEnabled(false);
        m_no_selection->toggle();
    }
}

void MainWindow::turnLabelBorder(Q3DSurface *graph)
{
    if (m_show_label_border->isChecked()) {
        graph->activeTheme()->setLabelBackgroundEnabled(true);
    } else {
        graph->activeTheme()->setLabelBackgroundEnabled(false);
    }
}

void MainWindow::setGradient(int gradient_number, Q3DSurface *graph)
{
    if (graph->seriesList().at(0)->isVisible()) {
        if (gradient_number == 1) {
            graph->seriesList().at(0)->setBaseGradient(m_gradient_color1);
            graph->seriesList().at(0)->setColorStyle(
                Q3DTheme::ColorStyleRangeGradient);
        } else {
            graph->seriesList().at(0)->setBaseGradient(m_gradient_color2);
            graph->seriesList().at(0)->setColorStyle(
                Q3DTheme::ColorStyleRangeGradient);
        }
    } else {
        if (gradient_number == 1) {
            graph->seriesList().at(1)->setBaseGradient(m_gradient_color1);
            graph->seriesList().at(1)->setColorStyle(
                Q3DTheme::ColorStyleRangeGradient);
        } else {
            graph->seriesList().at(1)->setBaseGradient(m_gradient_color2);
            graph->seriesList().at(1)->setColorStyle(
                Q3DTheme::ColorStyleRangeGradient);
        }
    }
}

void MainWindow::saveSettings()
{
    m_settings.setValue(QString("m_english"), m_english->isChecked());
    m_settings.setValue(QString("m_russian"), m_russian->isChecked());
    m_settings.setValue(QString("m_show_grid"), m_show_grid->isChecked());
    m_settings.setValue(QString("m_show_label"), m_show_label->isChecked());
    m_settings.setValue(QString("m_show_label_border"), m_show_label_border->isChecked());
    m_settings.setValue(QString("m_sinc1"), m_sinc1->isChecked());
    m_settings.setValue(QString("m_sinc2"), m_sinc2->isChecked());
    m_settings.setValue(QString("m_no_selection"), m_no_selection->isChecked());
    m_settings.setValue(QString("m_item"), m_item->isChecked());
    m_settings.setValue(QString("m_column_range_min"), m_column_range_min->value());
    m_settings.setValue(QString("m_column_range_max"), m_column_range_max->value());
    m_settings.setValue(QString("m_row_range_min"), m_row_range_min->value());
    m_settings.setValue(QString("m_row_range_max"), m_row_range_max->value());
}

void MainWindow::uploadSettings()
{
    m_english->setChecked(m_settings.value(QString("m_english"), false).toBool());
    m_russian->setChecked(m_settings.value(QString("m_russian"), false).toBool());
    m_show_grid->setChecked(m_settings.value(QString("m_show_grid"), false).toBool());
    m_show_label->setChecked(m_settings.value(QString("m_show_label"), false).toBool());
    m_show_label_border->setChecked(
        m_settings.value(QString("m_show_label_border"), false).toBool());
    m_sinc1->setChecked(m_settings.value(QString("m_sinc1"), false).toBool());
    m_sinc2->setChecked(m_settings.value(QString("m_sinc2"), false).toBool());
    m_no_selection->setChecked(m_settings.value(QString("m_no_selection"), false).toBool());
    m_item->setChecked(m_settings.value(QString("m_item"), false).toBool());
    m_column_range_min->setValue(m_settings.value(QString("m_column_range_min"), 0).toInt());
    m_column_range_max->setValue(m_settings.value(QString("m_column_range_max"), 50).toInt());
    m_row_range_min->setValue(m_settings.value(QString("m_row_range_min"), 0).toInt());
    m_row_range_max->setValue(m_settings.value(QString("m_row_range_max"), 50).toInt());
}

void MainWindow::setLanguage(int lang)
{
    if (lang == 1) {
        m_language->setTitle(QString("Language"));
        m_menu->setTitle(QString("&Settings"));
        m_save_action->setText(QString("&Save"));
        m_upload_action->setText(QString("&Upload"));
        m_show_grid->setText(QString("Show grid"));
        m_show_label->setText(QString("Show label"));
        m_show_label_border->setText(QString("Show label border"));
        m_plot->setTitle(QString("Plot"));
        m_no_selection->setText(QString("No selection"));
        m_item->setText(QString("Item"));
        m_selection_mode->setTitle(QString("Selection Mode"));
        m_colomn_range->setTitle(QString("Column range"));
        m_row_range->setTitle(QString("Row range"));
        m_custom_gradient->setTitle(QString("Custom gradient"));
    } else {
        m_language->setTitle(QString("Язык"));
        m_menu->setTitle(QString("&Настройки"));
        m_save_action->setText(QString("&Сохранить"));
        m_upload_action->setText(QString("&Загрузить"));
        m_show_grid->setText(QString("Отображать сетку"));
        m_show_label->setText(QString("Отображать метки"));
        m_show_label_border->setText(QString("Отображать границы меток"));
        m_plot->setTitle(QString("График"));
        m_no_selection->setText(QString("Без выбора"));
        m_item->setText(QString("Выбор точки"));
        m_selection_mode->setTitle(QString("Режим выбора"));
        m_colomn_range->setTitle(QString("Диапазон столбцов"));
        m_row_range->setTitle(QString("Диапазон строк"));
        m_custom_gradient->setTitle(QString("Пользовательский градиент"));
    }
}
