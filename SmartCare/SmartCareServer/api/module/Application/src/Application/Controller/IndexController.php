<?php
/**
 * Zend Framework (http://framework.zend.com/)
 *
 * @link      http://github.com/zendframework/ZendSkeletonApplication for the canonical source repository
 * @copyright Copyright (c) 2005-2015 Zend Technologies USA Inc. (http://www.zend.com)
 * @license   http://framework.zend.com/license/new-bsd New BSD License
 */

namespace Application\Controller;

use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\ViewModel;

class IndexController extends AbstractActionController
{
    public function indexAction()
    {

        $infos = array(
            'patient' => array(
                array(
                    'method' => 'GET',
                    'param' => '-',
                    'pattern' => 'patient.json',
                    'example' => 'patient.json',
                    'text' => 'alle Patienten'
                ),
                array(
                    'method' => 'GET',
                    'param' => 'patientId',
                    'pattern' => 'patient.json/{patientId}',
                    'example' => 'patient.json/1',
                    'text' => 'Daten eines Patienten'
                ),
                array(
                    'method' => 'PUT',
                    'param' => array(
                        'patientId' => 'PatientenID',
                        'isAsleep' => 'Boolean'
                    ),
                    'pattern' => 'patient.json/{patientId}',
                    'example' => '',
                    'text' => 'Patient ändern/updaten'
                ),
            ),
            'heartrate' => array(
                array(
                    'method' => 'GET',
                    'param' => '-',
                    'pattern' => 'heartrate.json',
                    'example' => 'heartrate.json',
                    'text' => 'alle Herzfrequenzen'
                ),
                array(
                    'method' => 'POST',
                    'param' => array(
                        'value' => 'int',
                        'patientid' => 'int',
                        'date' => 'Y-m-d H:i:s',
                    ),
                    'pattern' => 'heartrate.json',
                    'example' => '',
                    'text' => 'Herzfrequenz anlegen'
                ),
            ),
            'device' => array(
                array(
                    'method' => 'GET',
                    'param' => '-',
                    'pattern' => 'device.json',
                    'example' => 'device.json',
                    'text' => 'alle möglichen Geräte'
                ),
            ),
            'carerpatient' => array(
                array(
                    'method' => 'GET',
                    'param' => array(
                        'carerid' => 'int (optional)',
                        'dateFrom'  => 'Y-m-d H:i:s (optional)',
                        'dateTo'    => 'Y-m-d H:i:s (optional)',
                    ),
                    'pattern' => 'carerpatient.json',
                    'example' => 'carerpatient.json',
                    'text' => 'alle Verknüpfungen Pfleger <-> Patient'
                ),
                array(
                    'method' => 'GET',
                    'param' => 'carerpatientId',
                    'pattern' => 'carerpatient.json/{carerpatientId}',
                    'example' => 'carerpatient.json/1',
                    'text' => 'Daten einer Verknüpfungen Pfleger <-> Patient'
                ),
            ),
            'patientdevice' => array(
                array(
                    'method' => 'GET',
                    'param' => array(
                        'patientid' => 'int (optional)',
                    ),
                    'pattern' => 'patientdevice.json',
                    'example' => 'patientdevice.json',
                    'text' => 'alle Verknüpfungen Patient <-> Geräte'
                ),
                array(
                    'method' => 'GET',
                    'param' => 'patientdeviceId',
                    'pattern' => 'patientdevice.json/{patientdeviceId}',
                    'example' => 'patientdevice.json/1',
                    'text' => 'Daten einer Verknüpfungen Patient <-> Geräte'
                ),
                array(
                    'method' => 'PUT',
                    'param' => array(
                        'patientdeviceId' => 'int',
                        'state' => 'int',
                    ),
                    'pattern' => 'patientdevice.json/{patientdeviceId}',
                    'example' => '',
                    'text' => 'Verknüpfung ändern'
                ),
            ),
            'sleepcyle' => array(
                array(
                    'method' => 'GET',
                    'param' => array(
                        'patientid' => 'int (optional)',
                        'dateFrom'  => 'Y-m-d H:i:s (optional)',
                        'dateTo'    => 'Y-m-d H:i:s (optional)',
                        'isAsleep'  => 'Boolean (optional)',
                    ),
                    'pattern' => 'sleepcycle.json',
                    'example' => 'sleepcycle.json',
                    'text' => 'alle Schlafphasen'
                ),
                array(
                    'method' => 'GET',
                    'param' => array(
                        'sleepcycleId' => 'int',
                    ),
                    'pattern' => 'sleepcycle.json/{sleepcycleId}',
                    'example' => 'sleepcycle.json/1',
                    'text' => 'alle Schlafphasen'
                ),
                array(
                    'method' => 'POST',
                    'param' => array(
                        'patientid' => 'int',
                        'dateFrom' => 'Y-m-d H:i:s',
                    ),
                    'pattern' => 'sleepcycle.json',
                    'example' => '',
                    'text' => 'Schlafphase anlegen'
                ),
                array(
                    'method' => 'PUT',
                    'param' => array(
                        'sleepcycleId' => 'int',
                        'dateTo' => 'Y-m-d H:i:s',
                    ),
                    'pattern' => 'sleepcycle.json/{sleepcycleId}',
                    'example' => '',
                    'text' => 'Schlafphase ändern'
                ),
            )
        );

        return new ViewModel(array(
            'infos' => $infos
        ));
    }


    public function apiAction()
    {
        return new ViewModel();
    }

}
