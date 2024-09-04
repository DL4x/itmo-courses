#include "mainwindow.h"
#include "plot.h"

#include <QApplication>

int main(int argc, char **argv)
{
    QApplication app(argc, argv);

    QCoreApplication::setOrganizationName("settings");
    QSettings::setDefaultFormat(QSettings::IniFormat);
    QSettings::setPath(QSettings::IniFormat,
                       QSettings::UserScope,
                       QCoreApplication::applicationDirPath());

    Plot *graphics = new Plot();
    graphics->createSinc1();
    graphics->createSinc2();

    MainWindow *window = new MainWindow();
    window->setButtons(graphics);
    window->resize(1920, 1080);
    window->show();

    return app.exec();
}
