<?php

namespace Main\Controller;

use Application\Entity\Patient;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class CarerPatientController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $carerModel = $this->getServiceLocator()->get('CarerModel');

        $carerDateFrom = $this->params()->fromQuery('dateFrom');
        $carerDateTo = $this->params()->fromQuery('dateTo');

        $carerCrits = array();
        if($carerDateFrom)
            $carerCrits['dateFrom'] = new \DateTime($carerDateFrom);
        if($carerDateTo)
            $carerCrits['dateTo'] = new \DateTime($carerDateTo);

        $carerId = $this->params()->fromQuery('carerId');
        if( $carerId != '' ){
            $result = $carerModel->getPatientsByCarer($carerId, $carerCrits);
        } else {
            $result = $carerModel->getCarerPatients($carerCrits);
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
        $carerModel = $this->getServiceLocator()->get('CarerModel');
        $result = $carerModel->getCarerPatientById($id);
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
        return array('not implemented');
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
