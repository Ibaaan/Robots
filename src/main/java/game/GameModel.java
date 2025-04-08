package game;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Модель описывающая движение робота к цели
 */
public class GameModel {
    private static final String PROPERTY_NAME = "model";
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private final PropertyChangeSupport propChangeDispatcher =
            new PropertyChangeSupport(this);


    private double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public void onModelUpdateEvent() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY,
                m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if ((angleToTarget - m_robotDirection > 0 &&
                angleToTarget - m_robotDirection < Math.PI)
                |
                (angleToTarget - m_robotDirection > -2 * Math.PI &&
                        angleToTarget - m_robotDirection < -Math.PI)) {
            angularVelocity = maxAngularVelocity;
        } else if ((angleToTarget - m_robotDirection < 0 &&
                angleToTarget - m_robotDirection > -Math.PI)
                |
                (angleToTarget - m_robotDirection < 2 * Math.PI &&
                        angleToTarget - m_robotDirection > Math.PI)) {
            angularVelocity = -maxAngularVelocity;
        }

        if (oppositeIfBug(velocity, angularVelocity)) {
            angularVelocity *= -1;
        }

        moveRobot(velocity, angularVelocity, 10);
        propChangeDispatcher.firePropertyChange(PROPERTY_NAME, true, false);
    }

    /**
     * Возвращает значение true, если целевая точка
     * находится в одном из кругов траектории,
     * в противном случае значение false
     */
    private boolean oppositeIfBug(double velocity, double angularVelocity) {
        double radiusTrajCircle = (velocity / angularVelocity);

        double diffXFromTargetTo1Center =
                m_robotPositionX - radiusTrajCircle *
                        Math.sin(m_robotDirection) - m_targetPositionX;
        double diffXFromTargetTo2Center =
                m_robotPositionX + radiusTrajCircle *
                        Math.sin(m_robotDirection) - m_targetPositionX;
        double diffYFromTargetTo1Center =
                m_robotPositionY + radiusTrajCircle *
                        Math.cos(m_robotDirection) - m_targetPositionY;
        double diffYFromTargetTo2Center =
                m_robotPositionY - radiusTrajCircle *
                        Math.cos(m_robotDirection) - m_targetPositionY;

        return diffXFromTargetTo1Center * diffXFromTargetTo1Center +
                diffYFromTargetTo1Center * diffYFromTargetTo1Center <
                radiusTrajCircle * radiusTrajCircle
                |
                diffXFromTargetTo2Center * diffXFromTargetTo2Center +
                        diffYFromTargetTo2Center * diffYFromTargetTo2Center <
                        radiusTrajCircle * radiusTrajCircle;
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    private int round(double value) {
        return (int) (value + 0.5);
    }

    private double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle - 2 * Math.PI >= 0.01) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public int getRobotX() {
        return round(m_robotPositionX);
    }

    public int getRobotY() {
        return round(m_robotPositionY);
    }

    public int getTargetX() {
        return m_targetPositionX;
    }

    public int getTargetY() {
        return m_targetPositionY;
    }

    public void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
        propChangeDispatcher.firePropertyChange(PROPERTY_NAME, true, false);
    }

    public double getDirection() {
        return m_robotDirection;
    }

    public void addTextChangeListener(PropertyChangeListener listener) {
        propChangeDispatcher.addPropertyChangeListener(PROPERTY_NAME, listener);
    }
}
