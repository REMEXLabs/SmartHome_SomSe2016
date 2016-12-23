<?php

namespace Main\Controller;

use Application\Entity\Patient;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class PatientDevicesController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $deviceModel = $this->getServiceLocator()->get('DeviceModel');

        $result = array();
        $patientId = $this->params()->fromQuery('patientId');
        if( $patientId != '' ){
            $result = $deviceModel->getDevicesByPatient($patientId);
        } else {
            $result = $deviceModel->getPatientDevices($patientId);
        }

		return $result;
	}

	/**
	 * Return single resource
	 *
	 * @param mixed $id
	 * @return mixed
	 */
	public function get($id) {
        $deviceModel = $this->getServiceLocator()->get('DeviceModel');
        $result = $deviceModel->getPatientDeviceById($id);
        return $result;
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {
        return array('not implemented');
    }

	/**
	 * Update an existing resource
	 *
	 * @param mixed $id
	 * @param mixed $data
	 * @return mixed
	 */
	public function update($id, $data) {
        $em = $this->getServiceLocator()->get('Doctrine\ORM\EntityManager');

        $result = array();
        $state = $data['state'];

        $deviceModel = $this->getServiceLocator()->get('DeviceModel');
        $patientDevice = $deviceModel->getPatientDeviceByIdObj($id);
        if($patientDevice != null){
            if($state!= '' && is_numeric($state)){
                $deviceState = $deviceModel->getDeviceStateByIdObj($state);
                $patientDevice->setState($deviceState);
            } else {
                return $result['error'] = 'Invalid State';
            }
            if(isset($data['value']))
                $patientDevice->setValue($data['value']);

            $em->persist($patientDevice);
            $em->flush();
            $result['success'] = array($data);
        } else {
            $result['error'] = 'PatientDevice not found with ID: '.$id;
        }
        return $result;
    }

	/**
	 * Delete an existing resource
	 *
	 * @param  mixed $id
	 * @return mixed
	 */
	public function delete($id) {
        return array('not implemented');
    }
}
