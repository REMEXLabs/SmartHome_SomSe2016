<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2015 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

namespace Application;

use Zend\Mvc\ModuleRouteListener;
use Zend\Mvc\MvcEvent;

class Module
{
    public function onBootstrap(MvcEvent $e)
    {
        $eventManager        = $e->getApplication()->getEventManager();
        $moduleRouteListener = new ModuleRouteListener();
        $moduleRouteListener->attach($eventManager);

        if(trim($e->getResponse()->toString()) != ""){
            $headers = $e->getResponse()->getHeaders();
            //$headers

                //->addHeaderLine('Access-Control-Allow-Origin','*')
                //->addHeaderLine('Access-Control-Allow-Methods','POST, PUT, DELETE, GET, OPTIONS');
        }
    }

    public function getConfig()
    {
        return include __DIR__ . '/config/module.config.php';
    }

    public function getServiceConfig()
    {
        return array(
            'factories' => array(
                'DeviceModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Device($em);
                    return $model;
                },
                'PatientModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Patient($em);
                    return $model;
                },
                'HeartrateModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Heartrate($em);
                    return $model;
                },
                'DeviceModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Device($em);
                    return $model;
                },
                'SleepcycleModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Sleepcycle($em);
                    return $model;
                },
                'CarerModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Carer($em);
                    return $model;
                },
                'NotificationModel' =>  function($sm) {
                    $em = $sm->get('Doctrine\ORM\EntityManager');
                    $model = new Model\Notification($em);
                    return $model;
                },
            ),
        );
    }

    public function getAutoloaderConfig()
    {
        return array(
            'Zend\Loader\StandardAutoloader' => array(
                'namespaces' => array(
                    __NAMESPACE__ => __DIR__ . '/src/' . __NAMESPACE__,
                ),
            ),
        );
    }
}
