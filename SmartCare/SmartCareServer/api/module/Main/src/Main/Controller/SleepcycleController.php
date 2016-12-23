<?php

namespace Main\Controller;

use Application\Entity\Patient;
use Application\Entity\Sleepcycle;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class SleepcycleController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $sleepcycleDateFrom = $this->params()->fromQuery('dateFrom');
        $sleepcycleDateTo = $this->params()->fromQuery('dateTo');
        $isAsleep = $this->params()->fromQuery('isAsleep');
        $patientId = $this->params()->fromQuery('patientId');

        $sleepcycleCrits = array();
        if($sleepcycleDateFrom)
            $sleepcycleCrits['dateFrom'] = new \DateTime($sleepcycleDateFrom);
        if($sleepcycleDateTo)
            $sleepcycleCrits['dateTo'] = new \DateTime($sleepcycleDateTo);
        $sleepcycleCrits['isAsleep'] = ($isAsleep == 'true') ? true : false ;

        $sleepcycleModel = $this->getServiceLocator()->get('SleepcycleModel');

        if( $patientId != '' ){
            $result = $sleepcycleModel->getSleepcyclesByPatient($patientId, $sleepcycleCrits);
        } else {
            $result = $sleepcycleModel->getSleepcycles($sleepcycleCrits);
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
        $sleepcycleModel = $this->getServiceLocator()->get('SleepcycleModel');
        $result = $sleepcycleModel->getSleepcycleById($id);
        return $result;
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {
        $result = array();

        $dateFrom = $data['dateFrom'];
        $patientId = $data['patientId'];

        $dateTimeFrom = null;
        try{
            $dateTimeFrom = new \DateTime($dateFrom);
        } catch (\Exception $e){
            $result['error'][] = 'invalid date';
        }

        if($dateTimeFrom && is_numeric($patientId)){
            $em = $this->getServiceLocator()->get('Doctrine\ORM\EntityManager');
            $sleepcycle = new Sleepcycle();
            $sleepcycle->setDatefrom($dateTimeFrom);

            $patientModel = $this->getServiceLocator()->get('PatientModel');
            $patient = $patientModel->getPatientByIdObj($patientId);
            $sleepcycle->setPatientid($patient);

            $em->persist($sleepcycle);
            $em->flush();
            $result['success'] = $data;
        } else  {
            $result['error'] = array(
                'msg' => 'Invalid POST Data',
                'data' => $data,
            );
        }
        return $result;
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
        $dateTo = $data['dateTo'];

        if($dateTo!= ''){
            $sleepcycleModel = $this->getServiceLocator()->get('SleepcycleModel');
            $sleepcycle = $sleepcycleModel->getSleepcycleByIdObj($id);
            //Todo: check greater then dateFrom
            $sleepcycle->setDateto(new \DateTime($dateTo));

            $em->persist($sleepcycle);
            $em->flush();

            $result['success'] = array('dateTo' => $dateTo);
        } else {
            $result['error'] = 'Invalid PUT Data';
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
