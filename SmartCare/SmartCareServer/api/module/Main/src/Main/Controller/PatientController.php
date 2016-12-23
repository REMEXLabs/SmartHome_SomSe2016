<?php

namespace Main\Controller;

use Application\Entity\Patient;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class PatientController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $patientModel = $this->getServiceLocator()->get('PatientModel');
        $patients = $patientModel->getPatients();
		return $patients;
	}

	/**
	 * Return single resource
	 *
	 * @param mixed $id
	 * @return mixed
	 */
	public function get($id) {
        $patientModel = $this->getServiceLocator()->get('PatientModel');
        $patient = $patientModel->getPatientById($id);

        // Devices
        $deviceModel = $this->getServiceLocator()->get('DeviceModel');
        $devices = $deviceModel->getDevicesByPatient($id);
        $patient[0]['devices'] = $devices;

        // SLEEPCYCLES
        $sleepcycleDateFrom = $this->params()->fromQuery('sleepcycleDateFrom');
        $sleepcycleDateTo = $this->params()->fromQuery('sleepcycleDateTo');

        $sleepcycleCrits = array();
        if($sleepcycleDateFrom)
            $sleepcycleCrits['dateFrom'] = new \DateTime($sleepcycleDateFrom);
        if($sleepcycleDateTo)
            $sleepcycleCrits['dateTo'] = new \DateTime($sleepcycleDateTo);

        $sleepcycleModel = $this->getServiceLocator()->get('SleepcycleModel');
        $sleepcycles = $sleepcycleModel->getSleepcyclesByPatient($id, $sleepcycleCrits);
        $patient[0]['sleepcycles'] = $sleepcycles;

        // HEARTRATES
        $heartrateDateFrom = $this->params()->fromQuery('sleepcycleDateFrom');
        $heartrateDateTo = $this->params()->fromQuery('sleepcycleDateTo');

        $heartrateCrits = array();
        if($heartrateDateFrom)
            $heartrateCrits['dateFrom'] = new \DateTime($heartrateDateFrom);
        if($heartrateDateTo)
            $heartrateCrits['dateTo'] = new \DateTime($heartrateDateTo);

        $heartrateModel = $this->getServiceLocator()->get('HeartrateModel');
        $heartrates = $heartrateModel->getHeartratesByPatient($id);
        $patient[0]['heartrates'] = $heartrates;

        return $patient;
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {}

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

        $patientModel = $this->getServiceLocator()->get('PatientModel');
        $patient = $patientModel->getPatientByIdObj($id);

        $isAsleep = false;
        if(isset($data['isAsleep'])){
            $isAsleep = ($data['isAsleep'] == 'true') ? true : false;
            $patient->setIsAsleep($isAsleep);
            $result['success'] = array('isAsleep' => $isAsleep);
        }

        $em->persist($patient);
        $em->flush();

        return $result;
    }

	/**
	 * Delete an existing resource
	 *
	 * @param  mixed $id
	 * @return mixed
	 */
	public function delete($id) {}
}
